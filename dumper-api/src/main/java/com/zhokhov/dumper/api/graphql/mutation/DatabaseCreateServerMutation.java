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
package com.zhokhov.dumper.api.graphql.mutation;

import com.zhokhov.dumper.api.graphql.error.SecurityError;
import com.zhokhov.dumper.api.graphql.error.code.SecurityErrorCode;
import com.zhokhov.dumper.api.graphql.input.DatabaseCreateInput;
import com.zhokhov.dumper.api.graphql.model.DatabaseGraph;
import com.zhokhov.dumper.api.graphql.payload.DatabaseCreatePayload;
import com.zhokhov.dumper.api.security.DumperSecurityService;
import com.zhokhov.dumper.data.jooq.tables.records.DatabaseRecord;
import com.zhokhov.dumper.data.repository.DatabaseRepository;
import graphql.kickstart.tools.GraphQLMutationResolver;

import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class DatabaseCreateServerMutation implements GraphQLMutationResolver {

    private final DatabaseRepository databaseRepository;
    private final DumperSecurityService dumperSecurityService;

    public DatabaseCreateServerMutation(DatabaseRepository databaseRepository,
                                        DumperSecurityService dumperSecurityService) {
        this.databaseRepository = databaseRepository;
        this.dumperSecurityService = dumperSecurityService;
    }

    public DatabaseCreatePayload databaseCreate(DatabaseCreateInput input) {
        if (!dumperSecurityService.isLoggedIn()) {
            // TODO
            return new DatabaseCreatePayload(new SecurityError(SecurityErrorCode.UNAUTHORIZED, "UNAUTHORIZED"));
        }

        String environment = input.getEnvironment().toLowerCase();

        Optional<DatabaseRecord> mainDatabase = Optional.empty();

        if (input.getMainDatabaseName() != null) {
            mainDatabase = databaseRepository.findByName(input.getMainDatabaseName());
        }

        String name = mainDatabase
                .map(record -> record.getName() + "-" + environment)
                .orElseGet(() -> input.getName().toLowerCase());

        DatabaseRecord databaseRecord = databaseRepository.create(
                name,
                input.getHost(),
                input.getPort(),
                input.getUsername(),
                input.getPassword(),
                input.getDbname(),
                environment,
                input.getDescription(),
                mainDatabase.map(DatabaseRecord::getId).orElse(null)
        );

        return new DatabaseCreatePayload(new DatabaseGraph(databaseRecord));
    }

}
