package com.zhokhov.dumper.api.graphql.mutation

import com.zhokhov.dumper.graphql.client.mutation.UserLogoutMutation
import com.zhokhov.dumper.graphql.client.type.SecurityErrorCode
import com.zhokhov.dumper.test.AbstractTest
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
class UserLogoutServerMutationTests : AbstractTest() {

    @Test
    fun `not authorized`() {
        /** WHEN **/
        val userLogoutResult = graphQlClient.blockingMutate(
                UserLogoutMutation.builder()
                        .build()
        )

        /** THEN **/
        assertNotNull(userLogoutResult).apply {
            assertFalse(hasErrors())
            assertNotNull(data).apply {
                assertNotNull(userLogout()).apply {
                    assertNotNull(error()).let {
                        assertTrue(it is UserLogoutMutation.AsSecurityError).apply {
                            assertEquals(SecurityErrorCode.UNAUTHORIZED, it.code())
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `user success logout`() {
        /** GIVEN **/
        createTestUserAndLogin()

        /** WHEN **/
        var userLogoutResult = graphQlClient.blockingMutate(
                UserLogoutMutation.builder()
                        .build()
        )

        /** THEN **/
        assertNotNull(userLogoutResult).apply {
            assertFalse(hasErrors())
            assertNotNull(data).apply {
                assertNotNull(userLogout()).apply {
                    assertNull(error())
                }
            }
        }

        /** WHEN **/
        userLogoutResult = graphQlClient.blockingMutate(
                UserLogoutMutation.builder()
                        .build()
        )

        /** THEN **/
        assertNotNull(userLogoutResult).apply {
            assertFalse(hasErrors())
            assertNotNull(data).apply {
                assertNotNull(userLogout()).apply {
                    assertNotNull(error()).let {
                        assertTrue(it is UserLogoutMutation.AsSecurityError).apply {
                            assertEquals(SecurityErrorCode.UNAUTHORIZED, it.code())
                        }
                    }
                }
            }
        }
    }

}
