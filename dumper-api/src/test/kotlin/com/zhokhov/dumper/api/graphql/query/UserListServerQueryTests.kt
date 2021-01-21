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

import com.zhokhov.dumper.graphql.client.query.UserListQuery
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
class UserListServerQueryTests : AbstractTest() {

    @Test
    fun `not authorized`() {
        /** WHEN **/
        val userListResult = graphQlClient.blockingQuery(
                UserListQuery.builder().build()
        )

        /** THEN **/
        assertNotNull(userListResult).apply {
            assertFalse(hasErrors())
            assertNotNull(data).apply {
                assertNotNull(userList()).apply {
                    assertNotNull(error()).let {
                        assertTrue(it is UserListQuery.AsSecurityError).apply {
                            assertEquals(SecurityErrorCode.UNAUTHORIZED, it.code())
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `users list`() {
        /** GIVEN **/
        createTestUserAndLogin()
        createUser("putin", "putin", "putin@test.com", "Vova", "Putin")
        createUser("trump", "trump", "trump@test.com", "Don", "Trump")

        /** WHEN **/
        val userListResult = graphQlClient.blockingQuery(
                UserListQuery.builder().build()
        )

        /** THEN **/
        assertNotNull(userListResult).apply {
            assertFalse(hasErrors())
            assertNotNull(data).apply {
                assertNotNull(userList()).apply {
                    assertNull(error())
                    assertNotNull(data()).apply {
                        assertEquals(3, size)
                        get(0).apply {
                            assertEquals("putin", username())
                            assertEquals("putin@test.com", email())
                            assertEquals("Vova", firstName())
                            assertEquals("Putin", lastName())
                        }
                        get(1).apply {
                            assertEquals("test", username())
                            assertEquals("test@test.com", email())
                            assertEquals("Alex", firstName())
                            assertEquals("Hu", lastName())
                        }
                        get(2).apply {
                            assertEquals("trump", username())
                            assertEquals("trump@test.com", email())
                            assertEquals("Don", firstName())
                            assertEquals("Trump", lastName())
                        }
                    }
                }
            }
        }
    }

}
