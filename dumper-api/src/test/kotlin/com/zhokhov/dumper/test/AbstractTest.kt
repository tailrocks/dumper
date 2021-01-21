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
package com.zhokhov.dumper.test

import com.zhokhov.dumper.api.security.PasswordEncoder
import com.zhokhov.dumper.data.jooq.enums.ExportDeclarationDestination
import com.zhokhov.dumper.data.jooq.enums.ExportItemReason
import com.zhokhov.dumper.data.jooq.tables.records.AccountRecord
import com.zhokhov.dumper.data.jooq.tables.records.DatabaseRecord
import com.zhokhov.dumper.data.repository.AccountRepository
import com.zhokhov.dumper.data.repository.DatabaseRepository
import com.zhokhov.dumper.data.repository.ExportDeclarationRepository
import com.zhokhov.dumper.data.repository.ExportItemRepository
import com.zhokhov.dumper.graphql.client.mutation.UserLoginMutation
import com.zhokhov.dumper.graphql.client.type.CustomType
import com.zhokhov.dumper.graphql.client.type.UserLoginInput
import com.zhokhov.jambalaya.graphql.apollo.GraphQlClient
import io.micronaut.runtime.server.EmbeddedServer
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.jupiter.api.BeforeEach
import java.net.CookieManager
import java.time.Duration
import javax.inject.Inject
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

abstract class AbstractTest {

    @Inject lateinit var embeddedServer: EmbeddedServer
    @Inject lateinit var accountRepository: AccountRepository
    @Inject lateinit var databaseRepository: DatabaseRepository
    @Inject lateinit var exportDeclarationRepository: ExportDeclarationRepository
    @Inject lateinit var exportItemRepository: ExportItemRepository
    @Inject lateinit var passwordEncoder: PasswordEncoder
    @Inject lateinit var dataTestService: DataTestService

    lateinit var graphQlClient: GraphQlClient

    @BeforeEach
    fun beforeEach() {
        dataTestService.clean()

        graphQlClient = createGraphQlClient()
    }

    fun createGraphQlClient(): GraphQlClient {
        val url = "http://${embeddedServer.host}:${embeddedServer.port}/graphql"

        val cookieHandler = CookieManager()

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val okHttpClient = OkHttpClient.Builder()
                .cookieJar(JavaNetCookieJar(cookieHandler))
                .addInterceptor(logging)
                .callTimeout(Duration.ofMinutes(5))
                .connectTimeout(Duration.ofMinutes(5))
                .readTimeout(Duration.ofMinutes(5))
                .writeTimeout(Duration.ofMinutes(5))
                .build()

        return GraphQlClient(url, CustomType.values(), okHttpClient).apply {
            timeout = Duration.ofMinutes(5)
        }
    }

    fun createTestUserAndLogin(): AccountRecord {
        val user = createTestUser()
        login("test", "test")
        return user
    }

    fun createTestUser(): AccountRecord {
        return createUser("test", "test", "test@test.com", "Alex", "Hu")
    }

    fun createUser(username: String, password: String, email: String, firstName: String, lastName: String): AccountRecord {
        val encodedPassword = passwordEncoder.encode(password)
        return accountRepository.create(username, encodedPassword, email, firstName, lastName)
    }

    fun login(username: String, password: String, graphQlClient: GraphQlClient = this.graphQlClient) {
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
                        assertEquals(username, username())
                    }
                }
            }
        }
    }

    // TODO use config here
    fun createTestDatabases(): DatabaseRecord {
        val mainDatabase = databaseRepository.create(
                "example",
                "127.0.0.1",
                19001,
                "postgres",
                "",
                "dumper_example_prod",
                "prod",
                "Prod db",
                null
        )

        databaseRepository.create(
                "example-stage",
                "127.0.0.1",
                19001,
                "postgres",
                "",
                "dumper_example_stage",
                "stage",
                "Stage db",
                mainDatabase.id
        )
        return mainDatabase
    }

    fun createExportDeclaration(user: AccountRecord, targetDatabase: DatabaseRecord) {
        val exportDeclaration = exportDeclarationRepository.create(
                user.id,
                "Test export",
                ExportDeclarationDestination.JSON,
                targetDatabase.id,
                null
        )

        exportItemRepository.create(
                exportDeclaration.id,
                ExportItemReason.REQUEST,
                "account",
                mapOf(Pair("id", "1"))
        )
    }

}
