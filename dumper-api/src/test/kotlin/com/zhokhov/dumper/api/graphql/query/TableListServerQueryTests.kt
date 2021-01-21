/*
 * Copyright 2021 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhokhov.dumper.api.graphql.query

import com.zhokhov.dumper.graphql.client.query.TableListQuery
import com.zhokhov.dumper.graphql.client.type.SecurityErrorCode
import com.zhokhov.dumper.graphql.client.type.TableListInput
import com.zhokhov.dumper.test.AbstractTest
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@MicronautTest(transactional = false)
class TableListServerQueryTests : AbstractTest() {

    @Test
    fun `not authorized`() {
        /** WHEN **/
        val tableListResult = graphQlClient.blockingQuery(
                TableListQuery.builder()
                        .input(
                                TableListInput.builder()
                                        .databaseName("example")
                                        .build()
                        )
                        .build()
        )

        /** THEN **/
        assertNotNull(tableListResult).apply {
            assertFalse(hasErrors())
            assertNotNull(data).apply {
                assertNotNull(tableList()).apply {
                    assertNotNull(error()).let {
                        assertTrue(it is TableListQuery.AsSecurityError).apply {
                            assertEquals(SecurityErrorCode.UNAUTHORIZED, it.code())
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `table list`() {
        /** GIVEN **/
        createTestUserAndLogin()
        createTestDatabases()

        /** WHEN **/
        val tableListResult = graphQlClient.blockingQuery(
                TableListQuery.builder()
                        .input(
                                TableListInput.builder()
                                        .databaseName("example")
                                        .build()
                        )
                        .build()
        )

        /** THEN **/
        assertNotNull(tableListResult).apply {
            assertEquals(false, isFromCache)
            assertNull(errors)
            assertNotNull(data).apply {
                assertNotNull(tableList()).apply {
                    assertNotNull(data()).apply {
                        assertEquals(5, size)
                        assertNotNull(get(0)).apply {
                            assertEquals("account", name())
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
                        }
                        assertNotNull(get(1)).apply {
                            assertEquals("category", name())
                            assertNotNull(columns()).apply {
                                assertEquals(10, size)
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
                                    assertEquals("path", name())
                                    assertEquals("text", kind())
                                }
                                assertNotNull(get(5)).apply {
                                    assertEquals("title", name())
                                    assertEquals("varchar", kind())
                                }
                                assertNotNull(get(6)).apply {
                                    assertEquals("description", name())
                                    assertEquals("text", kind())
                                }
                                assertNotNull(get(7)).apply {
                                    assertEquals("parent_id", name())
                                    assertEquals("int8", kind())
                                }
                                assertNotNull(get(8)).apply {
                                    assertEquals("sort_order", name())
                                    assertEquals("int4", kind())
                                }
                                assertNotNull(get(9)).apply {
                                    assertEquals("visible", name())
                                    assertEquals("bool", kind())
                                }
                            }
                        }
                        assertNotNull(get(2)).apply {
                            assertEquals("flyway_schema_history", name())
                            assertNotNull(columns()).apply {
                                assertEquals(10, size)
                                assertNotNull(get(0)).apply {
                                    assertEquals("installed_rank", name())
                                    assertEquals("int4", kind())
                                }
                                assertNotNull(get(1)).apply {
                                    assertEquals("version", name())
                                    assertEquals("varchar", kind())
                                }
                                assertNotNull(get(2)).apply {
                                    assertEquals("description", name())
                                    assertEquals("varchar", kind())
                                }
                                assertNotNull(get(3)).apply {
                                    assertEquals("type", name())
                                    assertEquals("varchar", kind())
                                }
                                assertNotNull(get(4)).apply {
                                    assertEquals("script", name())
                                    assertEquals("varchar", kind())
                                }
                                assertNotNull(get(5)).apply {
                                    assertEquals("checksum", name())
                                    assertEquals("int4", kind())
                                }
                                assertNotNull(get(6)).apply {
                                    assertEquals("installed_by", name())
                                    assertEquals("varchar", kind())
                                }
                                assertNotNull(get(7)).apply {
                                    assertEquals("installed_on", name())
                                    assertEquals("timestamp", kind())
                                }
                                assertNotNull(get(8)).apply {
                                    assertEquals("execution_time", name())
                                    assertEquals("int4", kind())
                                }
                                assertNotNull(get(9)).apply {
                                    assertEquals("success", name())
                                    assertEquals("bool", kind())
                                }
                            }
                        }
                        assertNotNull(get(3)).apply {
                            assertEquals("post", name())
                            assertNotNull(columns()).apply {
                                assertEquals(10, size)
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
                                    assertEquals("title", name())
                                    assertEquals("text", kind())
                                }
                                assertNotNull(get(5)).apply {
                                    assertEquals("content", name())
                                    assertEquals("text", kind())
                                }
                                assertNotNull(get(6)).apply {
                                    assertEquals("author_id", name())
                                    assertEquals("int8", kind())
                                }
                                assertNotNull(get(7)).apply {
                                    assertEquals("topic_id", name())
                                    assertEquals("int8", kind())
                                }
                                assertNotNull(get(8)).apply {
                                    assertEquals("parent_id", name())
                                    assertEquals("int8", kind())
                                }
                                assertNotNull(get(9)).apply {
                                    assertEquals("visible", name())
                                    assertEquals("bool", kind())
                                }
                            }
                        }
                        assertNotNull(get(4)).apply {
                            assertEquals("topic", name())
                            assertNotNull(columns()).apply {
                                assertEquals(15, size)
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
                                    assertEquals("title", name())
                                    assertEquals("text", kind())
                                }
                                assertNotNull(get(5)).apply {
                                    assertEquals("content", name())
                                    assertEquals("text", kind())
                                }
                                assertNotNull(get(6)).apply {
                                    assertEquals("post_count", name())
                                    assertEquals("int4", kind())
                                }
                                assertNotNull(get(7)).apply {
                                    assertEquals("slug", name())
                                    assertEquals("text", kind())
                                }
                                assertNotNull(get(8)).apply {
                                    assertEquals("author_id", name())
                                    assertEquals("int8", kind())
                                }
                                assertNotNull(get(9)).apply {
                                    assertEquals("category_id", name())
                                    assertEquals("int8", kind())
                                }
                                assertNotNull(get(10)).apply {
                                    assertEquals("last_post_id", name())
                                    assertEquals("int8", kind())
                                }
                                assertNotNull(get(11)).apply {
                                    assertEquals("post_date", name())
                                    assertEquals("timestamp", kind())
                                }
                                assertNotNull(get(12)).apply {
                                    assertEquals("last_post_date", name())
                                    assertEquals("timestamp", kind())
                                }
                                assertNotNull(get(13)).apply {
                                    assertEquals("visible", name())
                                    assertEquals("bool", kind())
                                }
                                assertNotNull(get(14)).apply {
                                    assertEquals("pinned", name())
                                    assertEquals("bool", kind())
                                }
                            }
                        }
                    }
                    assertNull(error())
                }
            }
            assertEquals(false, hasErrors())
        }
    }

}
