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
import com.zhokhov.dumper.api.graphql.input.TableListInput;
import com.zhokhov.dumper.api.graphql.model.TableColumnGraph;
import com.zhokhov.dumper.api.graphql.model.TableGraph;
import com.zhokhov.dumper.api.graphql.payload.TableListPayload;
import com.zhokhov.dumper.api.security.DumperSecurityService;
import com.zhokhov.dumper.data.jooq.tables.records.DatabaseRecord;
import com.zhokhov.dumper.data.repository.DatabaseRepository;
import com.zhokhov.dumper.schema.Schema;
import com.zhokhov.dumper.schema.SchemaReader;
import graphql.kickstart.tools.GraphQLQueryResolver;

import javax.inject.Singleton;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
public class TableListServerQuery implements GraphQLQueryResolver {

    private final DatabaseRepository databaseRepository;
    private final DumperSecurityService dumperSecurityService;

    public TableListServerQuery(DatabaseRepository databaseRepository,
                                DumperSecurityService dumperSecurityService) {
        this.databaseRepository = databaseRepository;
        this.dumperSecurityService = dumperSecurityService;
    }

    public TableListPayload tableList(TableListInput input) {
        if (!dumperSecurityService.isLoggedIn()) {
            // TODO
            return new TableListPayload(new SecurityError(SecurityErrorCode.UNAUTHORIZED, "UNAUTHORIZED"));
        }

        Optional<DatabaseRecord> databaseRecord = databaseRepository.findByName(input.getDatabaseName());

        if (databaseRecord.isEmpty()) {
            // TODO
            throw new UnsupportedOperationException("Database not found");
        }

        String jdbcUrl = "jdbc:postgresql://" + databaseRecord.get().getHost() + ":" +
                databaseRecord.get().getPort() + "/" + databaseRecord.get().getDbname();
        String username = databaseRecord.get().getUsername();
        String password = databaseRecord.get().getPassword();

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            Schema schema = SchemaReader.read(connection);

            List<TableGraph> tableGraphList = schema.getTables().stream()
                    .map(table -> {
                        List<TableColumnGraph> columnGraphList = table.getColumns().stream()
                                .map(it -> new TableColumnGraph(it.getName(), it.getType()))
                                .collect(Collectors.toList());

                        return new TableGraph(table.getName(), columnGraphList);
                    })
                    .collect(Collectors.toList());

            return new TableListPayload(tableGraphList);
        } catch (SQLException | IOException e) {
            // TODO
            throw new UnsupportedOperationException(e);
        }
    }

}
