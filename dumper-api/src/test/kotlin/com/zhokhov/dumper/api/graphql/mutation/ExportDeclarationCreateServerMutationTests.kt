package com.zhokhov.dumper.api.graphql.mutation

import com.zhokhov.dumper.graphql.client.mutation.DatabaseCreateMutation
import com.zhokhov.dumper.graphql.client.mutation.ExportDeclarationCreateMutation
import com.zhokhov.dumper.graphql.client.type.ExportDeclarationCreateInput
import com.zhokhov.dumper.graphql.client.type.ExportDeclarationDestination
import com.zhokhov.dumper.graphql.client.type.ExportDeclarationStatus
import com.zhokhov.dumper.graphql.client.type.ExportItemInput
import com.zhokhov.dumper.graphql.client.type.FieldValueInput
import com.zhokhov.dumper.graphql.client.type.SecurityErrorCode
import com.zhokhov.dumper.test.AbstractTest
import com.zhokhov.dumper.test.ProdDataTestService
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Test
import javax.inject.Inject
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@MicronautTest(transactional = false)
class ExportDeclarationCreateServerMutationTests : AbstractTest() {

    @Inject lateinit var prodDataTestService: ProdDataTestService

    @Test
    fun `not authorized`() {
        /** WHEN **/
        val exportDeclarationCreateResult = graphQlClient.blockingMutate(
                ExportDeclarationCreateMutation.builder()
                        .input(
                                ExportDeclarationCreateInput.builder()
                                        .description("Test")
                                        .destination(ExportDeclarationDestination.JSON)
                                        .sourceDatabaseName("test")
                                        .items(
                                                listOf(
                                                        ExportItemInput.builder()
                                                                .sourceTableName("account")
                                                                .sourcePrimaryKey(
                                                                        listOf(
                                                                                FieldValueInput.builder()
                                                                                        .field("id")
                                                                                        .value("1")
                                                                                        .build()
                                                                        )
                                                                )
                                                                .build()
                                                )
                                        )
                                        .build()
                        )
                        .build()
        )

        /** THEN **/
        assertNotNull(exportDeclarationCreateResult).apply {
            assertFalse(hasErrors())
            assertNotNull(data).apply {
                assertNotNull(exportDeclarationCreate()).apply {
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
    fun `export declaration create`() {
        /** GIVEN **/
        createTestUserAndLogin()
        createTestDatabases()

        prodDataTestService.createAdminAccount()

        /** WHEN **/
        val exportDeclarationCreateResult = graphQlClient.blockingMutate(
                ExportDeclarationCreateMutation.builder()
                        .input(
                                ExportDeclarationCreateInput.builder()
                                        .description("Export Account #1")
                                        .destination(ExportDeclarationDestination.JSON)
                                        .sourceDatabaseName("example")
                                        .items(
                                                listOf(
                                                        ExportItemInput.builder()
                                                                .sourceTableName("account")
                                                                .sourcePrimaryKey(
                                                                        listOf(
                                                                                FieldValueInput.builder()
                                                                                        .field("id")
                                                                                        .value("1")
                                                                                        .build()
                                                                        )
                                                                )
                                                                .build()
                                                )
                                        )
                                        .build()
                        )
                        .build()
        )

        /** THEN **/
        assertNotNull(exportDeclarationCreateResult).apply {
            assertEquals(false, isFromCache)
            assertEquals(false, hasErrors())
            assertNull(errors)
            assertNotNull(data).apply {
                assertNotNull(exportDeclarationCreate()).apply {
                    assertNotNull(data()).apply {
                        assertEquals(ExportDeclarationStatus.PENDING, status())
                        assertEquals("Export Account #1", description())
                        assertNotNull(exporter()).apply {
                            assertEquals("Hu", lastName())
                            assertEquals("test", username())
                            assertEquals("test@test.com", email())
                            assertEquals("Alex", firstName())
                        }
                        assertEquals(ExportDeclarationDestination.JSON, destination())
                    }
                    assertNull(error())
                }
            }
        }
    }

}
