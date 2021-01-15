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
class UserListServerMutationTests : AbstractTest() {

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
