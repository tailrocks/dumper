package com.zhokhov.dumper.api.graphql.query

import com.zhokhov.dumper.graphql.client.query.ExportDeclarationListQuery
import com.zhokhov.dumper.graphql.client.type.ExportDeclarationDestination
import com.zhokhov.dumper.graphql.client.type.ExportDeclarationStatus
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
class ExportDeclarationListServerQueryTests : AbstractTest() {

    @Test
    fun `not authorized`() {
        /** WHEN **/
        val exportDeclarationListResult = graphQlClient.blockingQuery(
                ExportDeclarationListQuery.builder()
                        .build()
        )

        /** THEN **/
        assertNotNull(exportDeclarationListResult).apply {
            assertFalse(hasErrors())
            assertNotNull(data).apply {
                assertNotNull(exportDeclarationList()).apply {
                    assertNotNull(error()).let {
                        assertTrue(it is ExportDeclarationListQuery.AsSecurityError).apply {
                            assertEquals(SecurityErrorCode.UNAUTHORIZED, it.code())
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `export declaration list`() {
        /** GIVEN **/
        val user = createTestUserAndLogin()
        val database = createTestDatabases()
        createExportDeclaration(user, database)

        /** WHEN **/
        val exportDeclarationListResult = graphQlClient.blockingQuery(
                ExportDeclarationListQuery.builder()
                        .build()
        )

        /** THEN **/
        assertNotNull(exportDeclarationListResult).apply {
            assertNull(errors)
            assertNotNull(data).apply {
                assertNotNull(exportDeclarationList()).apply {
                    assertNotNull(data()).apply {
                        assertEquals(1, size)
                        assertNotNull(get(0)).apply {
                            assertEquals(ExportDeclarationStatus.PENDING, status())
                            assertEquals("Test export", description())
                            assertNotNull(exporter()).apply {
                                assertEquals("Hu", lastName())
                                assertEquals("test", username())
                                assertEquals("test@test.com", email())
                                assertEquals("Alex", firstName())
                            }
                            assertEquals(ExportDeclarationDestination.JSON, destination())
                        }
                    }
                    assertNull(error())
                }
            }
            assertEquals(false, hasErrors())
            assertEquals(false, isFromCache)
        }
    }

}
