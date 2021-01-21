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
