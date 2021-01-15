package com.zhokhov.dumper.api.graphql.mutation

import com.zhokhov.dumper.graphql.client.mutation.UserCreateMutation
import com.zhokhov.dumper.graphql.client.type.SecurityErrorCode
import com.zhokhov.dumper.graphql.client.type.UserCreateInput
import com.zhokhov.dumper.test.AbstractTest
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

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
