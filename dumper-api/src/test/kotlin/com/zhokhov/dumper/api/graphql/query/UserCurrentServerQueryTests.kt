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

import com.zhokhov.dumper.graphql.client.query.UserCurrentQuery
import com.zhokhov.dumper.graphql.client.type.SecurityErrorCode
import com.zhokhov.dumper.test.AbstractTest
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@MicronautTest(transactional = false)
class UserCurrentServerQueryTests : AbstractTest() {

    @Test
    fun `not authorized`() {
        /** WHEN **/
        val userCurrentResult = graphQlClient.blockingQuery(
                UserCurrentQuery.builder().build()
        )

        /** THEN **/
        assertNotNull(userCurrentResult).apply {
            assertFalse(hasErrors())
            assertNotNull(data).apply {
                assertNotNull(userCurrent()).apply {
                    assertNotNull(error()).let {
                        assertTrue(it is UserCurrentQuery.AsSecurityError).apply {
                            assertEquals(SecurityErrorCode.UNAUTHORIZED, it.code())
                        }
                    }
                }
            }
        }
    }

}
