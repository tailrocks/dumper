package com.zhokhov.dumper.api.graphql.query

import com.zhokhov.dumper.graphql.client.query.UserCurrentQuery
import com.zhokhov.dumper.graphql.client.type.CustomType
import com.zhokhov.dumper.graphql.client.type.SecurityErrorCode
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
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
class UserCurrentServerMutationTests {

    private lateinit var graphQlClient: GraphQlClient
    @Inject lateinit var embeddedServer: EmbeddedServer
    @Inject lateinit var dataTestService: DataTestService

    @BeforeAll
    fun init() {
        dataTestService.clean()

        val url = "http://${embeddedServer.host}:${embeddedServer.port}/graphql"
        graphQlClient = GraphQlClient(url, CustomType.values(), null)
    }

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
