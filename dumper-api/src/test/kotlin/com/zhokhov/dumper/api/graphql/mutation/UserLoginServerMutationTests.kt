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

import com.zhokhov.dumper.graphql.client.mutation.UserLoginMutation
import com.zhokhov.dumper.graphql.client.query.UserCurrentQuery
import com.zhokhov.dumper.graphql.client.type.UserLoginInput
import com.zhokhov.dumper.test.AbstractTest
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@MicronautTest(transactional = false)
class UserLoginServerMutationTests : AbstractTest() {

    @Test
    fun `user login`() {
        /** GIVEN **/
        createTestUser()

        /** WHEN **/
        val userLoginResult = graphQlClient.blockingMutate(
            UserLoginMutation.builder()
                .input(
                    UserLoginInput.builder()
                        .username("test")
                        .password("test")
                        .build()
                )
                .build()
        )

        /** THEN **/
        assertNotNull(userLoginResult).apply {
            assertFalse(hasErrors())
            assertNotNull(data).apply {
                assertNotNull(userLogin()).apply {
                    assertNull(error())
                    assertNotNull(data()).apply {
                        assertEquals("test", username())
                        assertEquals("test@test.com", email())
                        assertEquals("Alex", firstName())
                        assertEquals("Hu", lastName())
                    }
                }
            }
        }

        /** WHEN **/
        val userCurrentResult = graphQlClient.blockingQuery(
            UserCurrentQuery.builder().build()
        )

        /** THEN **/
        assertNotNull(userCurrentResult).apply {
            assertFalse(hasErrors())
            assertNotNull(data).apply {
                assertNotNull(userCurrent()).apply {
                    assertNull(error())
                    assertNotNull(data()).apply {
                        assertEquals("test", username())
                        assertEquals("test@test.com", email())
                        assertEquals("Alex", firstName())
                        assertEquals("Hu", lastName())
                    }
                }
            }
        }
    }

}
