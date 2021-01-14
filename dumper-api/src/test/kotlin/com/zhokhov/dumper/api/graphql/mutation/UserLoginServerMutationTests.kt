package com.zhokhov.dumper.api.graphql.mutation

import com.zhokhov.dumper.data.repository.AccountRepository
import com.zhokhov.dumper.graphql.client.mutation.UserLoginMutation
import com.zhokhov.dumper.graphql.client.type.CustomType
import com.zhokhov.dumper.graphql.client.type.LoginErrorCode
import com.zhokhov.dumper.graphql.client.type.UserLoginInput
import com.zhokhov.dumper.test.DataTestService
import com.zhokhov.jambalaya.graphql.apollo.GraphQlClient
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import javax.inject.Inject
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
class UserLoginServerMutationTests {

    private lateinit var graphQlClient: GraphQlClient

    @Inject
    private lateinit var embeddedServer: EmbeddedServer

    @Inject
    private lateinit var accountRepository: AccountRepository

    @Inject
    private lateinit var dataTestService: DataTestService

    @BeforeAll
    fun init() {
        dataTestService.clean()

        val url = "http://${embeddedServer.host}:${embeddedServer.port}/graphql"
        graphQlClient = GraphQlClient(url, CustomType.values())
    }

    @Test
    fun `user not found`() {
        /** GIVEN **/
        val account = accountRepository.create("test", "test", "test@test.com", "Alex", "Hu")

        /** WHEN **/
        val input = UserLoginInput.builder()
                .username("test")
                .password("test")
                .build()

        val loginMutation = UserLoginMutation.builder()
                .input(input)
                .build()

        val result = graphQlClient.blockingMutate(loginMutation)

        /** THEN **/
        assertNotNull(result).apply {
            assertNull(errors)
            assertNotNull(data).apply {
                assertNotNull(userLogin()).apply {
                    assertNull(data())
                    assertNotNull(error()).let {
                        assertTrue(it is UserLoginMutation.AsLoginError).apply {
                            assertEquals(LoginErrorCode.USER_NOT_FOUND, it.code())
                        }
                    }
                }
            }
        }
    }

}
