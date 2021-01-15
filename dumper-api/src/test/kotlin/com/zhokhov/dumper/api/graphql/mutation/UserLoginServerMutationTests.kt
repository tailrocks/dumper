package com.zhokhov.dumper.api.graphql.mutation

import com.zhokhov.dumper.api.security.PasswordEncoder
import com.zhokhov.dumper.data.repository.AccountRepository
import com.zhokhov.dumper.graphql.client.mutation.UserLoginMutation
import com.zhokhov.dumper.graphql.client.query.UserCurrentQuery
import com.zhokhov.dumper.graphql.client.type.CustomType
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
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
class UserLoginServerMutationTests {

    private lateinit var graphQlClient: GraphQlClient

    @Inject
    private lateinit var embeddedServer: EmbeddedServer

    @Inject
    private lateinit var accountRepository: AccountRepository

    @Inject
    private lateinit var passwordEncoder: PasswordEncoder

    @Inject
    private lateinit var dataTestService: DataTestService

    @BeforeAll
    fun init() {
        dataTestService.clean()

        val url = "http://${embeddedServer.host}:${embeddedServer.port}/graphql"
        graphQlClient = GraphQlClient(url, CustomType.values(), null)
    }

    @Test
    fun `user login`() {
        /** GIVEN **/
        val password = passwordEncoder.encode("test")
        accountRepository.create("test", password, "test@test.com", "Alex", "Hu")

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
