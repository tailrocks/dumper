package com.zhokhov.dumper.api.graphql.mutation

import com.zhokhov.dumper.graphql.client.mutation.DatabaseCreateMutation
import com.zhokhov.dumper.graphql.client.type.DatabaseCreateInput
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
class DatabaseCreateServerMutationTests : AbstractTest() {

    @Test
    fun `not authorized`() {
        /** WHEN **/
        val databaseCreateResult = graphQlClient.blockingMutate(
                DatabaseCreateMutation.builder()
                        .input(
                                DatabaseCreateInput.builder()
                                        .name("polusharie")
                                        .host("127.0.0.1")
                                        .port(5432)
                                        .username("postgres")
                                        .password("")
                                        .dbname("polusharie")
                                        .environment("prod")
                                        .description("Prod database")
                                        .build()
                        )
                        .build()
        )

        /** THEN **/
        assertNotNull(databaseCreateResult).apply {
            assertFalse(hasErrors())
            assertNotNull(data).apply {
                assertNotNull(databaseCreate()).apply {
                    assertNotNull(error()).let {
                        assertTrue(it is DatabaseCreateMutation.AsSecurityError).apply {
                            assertEquals(SecurityErrorCode.UNAUTHORIZED, it.code())
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `database create`() {
        /** GIVEN **/
        createTestUserAndLogin()

        /** WHEN **/
        var databaseCreateResult = graphQlClient.blockingMutate(
                DatabaseCreateMutation.builder()
                        .input(
                                DatabaseCreateInput.builder()
                                        .name("polusharie")
                                        .host("127.0.0.1")
                                        .port(5432)
                                        .username("postgres")
                                        .password("")
                                        .dbname("polusharie")
                                        .environment("prod")
                                        .description("Prod database")
                                        .build()
                        )
                        .build()
        )

        /** THEN **/
        assertNotNull(databaseCreateResult).apply {
            assertFalse(hasErrors())
            assertNotNull(data).apply {
                assertNotNull(databaseCreate()).apply {
                    assertNull(error())
                    assertNotNull(data()).apply {
                        assertEquals("polusharie", name())
                        assertEquals("127.0.0.1", host())
                        assertEquals(5432, port())
                        assertEquals("postgres", username())
                        assertEquals("polusharie", dbname())
                        assertEquals("prod", environment())
                        assertEquals("Prod database", description())
                        assertTrue(subDatabases().isEmpty())
                    }
                }
            }
        }

        /** WHEN **/
        // add another environment
        databaseCreateResult = graphQlClient.blockingMutate(
                DatabaseCreateMutation.builder()
                        .input(
                                DatabaseCreateInput.builder()
                                        .mainDatabaseName("polusharie")
                                        .host("127.0.0.2")
                                        .port(5432)
                                        .username("postgres")
                                        .password("")
                                        .dbname("polusharie")
                                        .environment("stage")
                                        .description("Stage database")
                                        .build()
                        )
                        .build()
        )

        /** THEN **/
        assertNotNull(databaseCreateResult).apply {
            assertFalse(hasErrors())
            assertNotNull(data).apply {
                assertNotNull(databaseCreate()).apply {
                    assertNull(error())
                    assertNotNull(data()).apply {
                        assertEquals("polusharie-stage", name())
                        assertEquals("127.0.0.2", host())
                        assertEquals(5432, port())
                        assertEquals("postgres", username())
                        assertEquals("polusharie", dbname())
                        assertEquals("stage", environment())
                        assertEquals("Stage database", description())
                        assertTrue(subDatabases().isEmpty())
                    }
                }
            }
        }
    }

}
