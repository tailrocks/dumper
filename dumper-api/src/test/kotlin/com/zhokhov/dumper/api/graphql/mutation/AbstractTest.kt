package com.zhokhov.dumper.api.graphql.mutation

import com.zhokhov.dumper.api.security.PasswordEncoder
import com.zhokhov.dumper.data.repository.AccountRepository
import com.zhokhov.dumper.graphql.client.mutation.UserLoginMutation
import com.zhokhov.dumper.graphql.client.type.CustomType
import com.zhokhov.dumper.graphql.client.type.UserLoginInput
import com.zhokhov.dumper.test.DataTestService
import com.zhokhov.jambalaya.graphql.apollo.GraphQlClient
import io.micronaut.runtime.server.EmbeddedServer
import org.junit.jupiter.api.BeforeAll
import javax.inject.Inject
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

abstract class AbstractTest {

    @Inject lateinit var embeddedServer: EmbeddedServer
    @Inject lateinit var accountRepository: AccountRepository
    @Inject lateinit var passwordEncoder: PasswordEncoder
    @Inject lateinit var dataTestService: DataTestService

    lateinit var graphQlClient: GraphQlClient

    @BeforeAll
    fun beforeAll() {
        dataTestService.clean()

        val url = "http://${embeddedServer.host}:${embeddedServer.port}/graphql"
        graphQlClient = GraphQlClient(url, CustomType.values(), null)
    }

    fun createTestUserAndLogin() {
        createTestUser()
        login("test", "test")
    }

    fun createTestUser() {
        val password = passwordEncoder.encode("test")
        accountRepository.create("test", password, "test@test.com", "Alex", "Hu")
    }

    fun login(username: String, password: String) {
        val userLoginResult = graphQlClient.blockingMutate(
                UserLoginMutation.builder()
                        .input(
                                UserLoginInput.builder()
                                        .username(username)
                                        .password(password)
                                        .build()
                        )
                        .build()
        )

        assertNotNull(userLoginResult).apply {
            assertFalse(hasErrors())
            assertNotNull(data).apply {
                assertNotNull(userLogin()).apply {
                    assertNull(error())
                    assertNotNull(data()).apply {
                        assertEquals("test", username())
                    }
                }
            }
        }
    }

}
