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

import com.zhokhov.dumper.graphql.client.query.DatabaseListQuery
import com.zhokhov.dumper.graphql.client.type.SecurityErrorCode
import com.zhokhov.dumper.test.AbstractTest
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Test
import kotlin.test.*

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
