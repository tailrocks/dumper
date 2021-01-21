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
package com.zhokhov.dumper.api.graphql.query;

import com.zhokhov.dumper.api.graphql.error.SecurityError;
import com.zhokhov.dumper.api.graphql.error.code.SecurityErrorCode;
import com.zhokhov.dumper.api.graphql.model.DatabaseGraph;
import com.zhokhov.dumper.api.graphql.payload.DatabaseListPayload;
import com.zhokhov.dumper.api.security.DumperSecurityService;
import com.zhokhov.dumper.data.jooq.tables.records.DatabaseRecord;
import com.zhokhov.dumper.data.repository.DatabaseRepository;
import graphql.kickstart.tools.GraphQLQueryResolver;

import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class DatabaseListServerQuery implements GraphQLQueryResolver {

    private final DatabaseRepository databaseRepository;
    private final DumperSecurityService dumperSecurityService;

    public DatabaseListServerQuery(DatabaseRepository databaseRepository, DumperSecurityService dumperSecurityService) {
        this.databaseRepository = databaseRepository;
        this.dumperSecurityService = dumperSecurityService;
    }

    public DatabaseListPayload databaseList() {
        if (!dumperSecurityService.isLoggedIn()) {
            // TODO
            return new DatabaseListPayload(new SecurityError(SecurityErrorCode.UNAUTHORIZED, "UNAUTHORIZED"));
        }

        List<DatabaseRecord> databaseRecordList = databaseRepository.findAllMainDatabasesOrderByName();

        List<DatabaseGraph> databaseGraphList = databaseRecordList.stream()
                .map(DatabaseGraph::new)
                .collect(Collectors.toList());

        return new DatabaseListPayload(databaseGraphList);
    }

}
