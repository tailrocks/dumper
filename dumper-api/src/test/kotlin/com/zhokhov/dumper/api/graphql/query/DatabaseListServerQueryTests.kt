package com.zhokhov.dumper.api.graphql.query

import com.zhokhov.dumper.graphql.client.query.DatabaseListQuery
import com.zhokhov.dumper.graphql.client.type.SecurityErrorCode
import com.zhokhov.dumper.test.AbstractTest
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@MicronautTest(transactional = false)
class DatabaseListServerQueryTests : AbstractTest() {

    @Test
    fun `not authorized`() {
        /** WHEN **/
        val databaseListResult = graphQlClient.blockingQuery(
                DatabaseListQuery.builder().build()
        )

        /** THEN **/
        assertNotNull(databaseListResult).apply {
            assertFalse(hasErrors())
            assertNotNull(data).apply {
                assertNotNull(databaseList()).apply {
                    assertNotNull(error()).let {
                        assertTrue(it is DatabaseListQuery.AsSecurityError).apply {
                            assertEquals(SecurityErrorCode.UNAUTHORIZED, it.code())
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `database list`() {
        /** GIVEN **/
        createTestUserAndLogin()
        createTestDatabases()

        /** WHEN **/
        val databaseListResult = graphQlClient.blockingQuery(
                DatabaseListQuery.builder().build()
        )

        /** THEN **/
        assertNotNull(databaseListResult).apply {
            assertFalse(hasErrors())
            assertNotNull(data).apply {
                assertNotNull(databaseList()).apply {
                    assertNull(error())
                    assertNotNull(data()).apply {
                        assertEquals(1, size)
                        get(0).apply {
                            assertEquals("example", name())
                            assertEquals("127.0.0.1", host())
                            assertEquals(19001, port())
                            assertEquals("postgres", username())
                            assertEquals("dumper_example_prod", dbname())
                            assertEquals("prod", environment())
                            assertEquals("Prod db", description())
                            assertNotNull(subDatabases()).apply {
                                assertEquals(1, size)
                                get(0).apply {
                                    assertEquals("example-stage", name())
                                    assertEquals("127.0.0.1", host())
                                    assertEquals(19001, port())
                                    assertEquals("postgres", username())
                                    assertEquals("dumper_example_stage", dbname())
                                    assertEquals("stage", environment())
                                    assertEquals("Stage db", description())
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
