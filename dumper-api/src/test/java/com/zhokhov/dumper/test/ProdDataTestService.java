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
package com.zhokhov.dumper.test;

import io.micronaut.transaction.annotation.TransactionalAdvice;
import org.jooq.DSLContext;

import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class ProdDataTestService {

    private final DSLContext dslContext;

    public ProdDataTestService(@Named("example-prod") DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @TransactionalAdvice("example-prod")
    public void createAdminAccount() {
        String query = "INSERT INTO \"public\".\"account\" (\"id\", \"created_date\", \"last_modified_date\", \"version\", \"username\", \"email\", \"password\", \"enabled\", \"gender\", \"roles\", \"registration_date\") VALUES\n" +
                "('1', '2021-01-20 20:09:47.558574', '2021-01-20 20:09:47.558574', '1', 'admin', 'admin@admin', " +
                "'admin', 't', 'MALE', '{ROLE_ADMIN}', '2021-01-20 20:09:47.558574');";

        dslContext.execute(query);
    }

}
