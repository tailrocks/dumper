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
