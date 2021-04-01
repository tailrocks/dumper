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
package com.zhokhov.dumper.api.graphql.mutation

import com.zhokhov.dumper.graphql.client.mutation.UserCreateMutation
import com.zhokhov.dumper.graphql.client.type.SecurityErrorCode
import com.zhokhov.dumper.graphql.client.type.UserCreateInput
import com.zhokhov.dumper.test.AbstractTest
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Test
import kotlin.test.*

@MicronautTest(transactional = false)
class UserCreateServerMutationTests : AbstractTest() {

    @Test
    fun `not authorized`() {
        /** WHEN **/
        val userCreateResult = graphQlClient.blockingMutate(
            UserCreateMutation.builder()
                .input(
                    UserCreateInput.builder()
                        .username("test")
                        .password("test")
                        .email("test@test.com")
                        .firstName("test")
                        .lastName("test")
                        .build()
                )
                .build()
        )

        /** THEN **/
        assertNotNull(userCreateResult).apply {
            assertFalse(hasErrors())
            assertNotNull(data).apply {
                assertNotNull(userCreate()).apply {
                    assertNotNull(error()).let {
                        assertTrue(it is UserCreateMutation.AsSecurityError).apply {
                            assertEquals(SecurityErrorCode.UNAUTHORIZED, it.code())
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `user create`() {
        /** GIVEN **/
        createTestUserAndLogin()

        /** WHEN **/
        val userCreateResult = graphQlClient.blockingMutate(
            UserCreateMutation.builder()
                .input(
                    UserCreateInput.builder()
                        .username("vasya")
                        .password("test")
                        .email("vasya@test.com")
                        .firstName("Vasilij")
                        .lastName("Myasov")
                        .build()
                )
                .build()
        )

        /** THEN **/
        assertNotNull(userCreateResult).apply {
            assertFalse(hasErrors())
            assertNotNull(data).apply {
                assertNotNull(userCreate()).apply {
                    assertNull(error())
                    assertNotNull(data()).apply {
                        assertEquals("vasya", username())
                        assertEquals("vasya@test.com", email())
                        assertEquals("Vasilij", firstName())
                        assertEquals("Myasov", lastName())
                    }
                }
            }
        }

        /** WHEN **/
        val newUserClient = createGraphQlClient()

        login("vasya", "test", newUserClient)
    }

}
