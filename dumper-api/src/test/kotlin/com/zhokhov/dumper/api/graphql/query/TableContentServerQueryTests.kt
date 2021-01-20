package com.zhokhov.dumper.api.graphql.query

import com.zhokhov.dumper.graphql.client.query.TableContentQuery
import com.zhokhov.dumper.graphql.client.type.SecurityErrorCode
import com.zhokhov.dumper.graphql.client.type.TableContentFilterInput
import com.zhokhov.dumper.graphql.client.type.TableContentInput
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
class TableContentServerQueryTests : AbstractTest() {

    @Inject lateinit var prodDataTestService: ProdDataTestService

    @Test
    fun `not authorized`() {
        /** WHEN **/
        val tableContentResult = graphQlClient.blockingQuery(
                TableContentQuery.builder()
                        .input(
                                TableContentInput.builder()
                                        .databaseName("example")
                                        .tableName("account")
                                        .filters(
                                                listOf(
                                                        TableContentFilterInput.builder()
                                                                .field("id")
                                                                .value("1")
                                                                .build()
                                                )
                                        )
                                        .build()
                        )
                        .build()
        )

        /** THEN **/
        assertNotNull(tableContentResult).apply {
            assertFalse(hasErrors())
            assertNotNull(data).apply {
                assertNotNull(tableContent()).apply {
                    assertNotNull(error()).let {
                        assertTrue(it is TableContentQuery.AsSecurityError).apply {
                            assertEquals(SecurityErrorCode.UNAUTHORIZED, it.code())
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `table content`() {
        /** GIVEN **/
        createTestUserAndLogin()
        createTestDatabases()

        prodDataTestService.createAdminAccount()

        /** WHEN **/
        val tableContentResult = graphQlClient.blockingQuery(
                TableContentQuery.builder()
                        .input(
                                TableContentInput.builder()
                                        .databaseName("example")
                                        .tableName("account")
                                        .filters(
                                                listOf(
                                                        TableContentFilterInput.builder()
                                                                .field("id")
                                                                .value("1")
                                                                .build()
                                                )
                                        )
                                        .build()
                        )
                        .build()
        )

        /** THEN **/
        assertNotNull(tableContentResult).apply {
            assertEquals(false, hasErrors())
            assertEquals(false, isFromCache)
            assertNull(errors)
            assertNotNull(data).apply {
                assertNotNull(tableContent()).apply {
                    assertNotNull(data()).apply {
                        assertNotNull(columns()).apply {
                            assertEquals(11, size)
                            assertNotNull(get(0)).apply {
                                assertEquals("id", name())
                                assertEquals("int8", kind())
                            }
                            assertNotNull(get(1)).apply {
                                assertEquals("created_date", name())
                                assertEquals("timestamp", kind())
                            }
                            assertNotNull(get(2)).apply {
                                assertEquals("last_modified_date", name())
                                assertEquals("timestamp", kind())
                            }
                            assertNotNull(get(3)).apply {
                                assertEquals("version", name())
                                assertEquals("int8", kind())
                            }
                            assertNotNull(get(4)).apply {
                                assertEquals("username", name())
                                assertEquals("varchar", kind())
                            }
                            assertNotNull(get(5)).apply {
                                assertEquals("email", name())
                                assertEquals("varchar", kind())
                            }
                            assertNotNull(get(6)).apply {
                                assertEquals("password", name())
                                assertEquals("varchar", kind())
                            }
                            assertNotNull(get(7)).apply {
                                assertEquals("enabled", name())
                                assertEquals("bool", kind())
                            }
                            assertNotNull(get(8)).apply {
                                assertEquals("gender", name())
                                assertEquals("gender", kind())
                            }
                            assertNotNull(get(9)).apply {
                                assertEquals("roles", name())
                                assertEquals("_authority", kind())
                            }
                            assertNotNull(get(10)).apply {
                                assertEquals("registration_date", name())
                                assertEquals("timestamp", kind())
                            }
                        }
                        assertNotNull(rows()).apply {
                            assertEquals(1, size)
                            assertNotNull(get(0)).apply {
                                assertNotNull(values()).apply {
                                    assertEquals(11, size)
                                    assertEquals("1", get(0))
                                    assertEquals("2021-01-20 20:09:47.558574", get(1))
                                    assertEquals("2021-01-20 20:09:47.558574", get(2))
                                    assertEquals("1", get(3))
                                    assertEquals("admin", get(4))
                                    assertEquals("admin@admin", get(5))
                                    assertEquals("admin", get(6))
                                    assertEquals("t", get(7))
                                    assertEquals("MALE", get(8))
                                    assertEquals("{ROLE_ADMIN}", get(9))
                                    assertEquals("2021-01-20 20:09:47.558574", get(10))
                                }
                                assertNotNull(primaryKey()).apply {
                                    assertEquals(1, size)
                                    assertNotNull(get(0)).apply {
                                        assertEquals("1", value())
                                        assertEquals("id", field())
                                    }
                                }
                            }
                        }
                    }
                    assertNull(error())
                }
            }
        }
    }

}
