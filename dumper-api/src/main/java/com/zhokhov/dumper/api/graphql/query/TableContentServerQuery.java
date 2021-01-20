package com.zhokhov.dumper.api.graphql.query;

import com.zhokhov.dumper.api.graphql.error.SecurityError;
import com.zhokhov.dumper.api.graphql.error.code.SecurityErrorCode;
import com.zhokhov.dumper.api.graphql.input.TableContentFilterInput;
import com.zhokhov.dumper.api.graphql.input.TableContentInput;
import com.zhokhov.dumper.api.graphql.model.FieldValueGraph;
import com.zhokhov.dumper.api.graphql.model.TableColumnGraph;
import com.zhokhov.dumper.api.graphql.model.TableContentPayloadDataGraph;
import com.zhokhov.dumper.api.graphql.model.TableRowGraph;
import com.zhokhov.dumper.api.graphql.payload.TableContentPayload;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
public class TableContentServerQuery implements GraphQLQueryResolver {

    private final DatabaseRepository databaseRepository;
    private final DumperSecurityService dumperSecurityService;

    public TableContentServerQuery(DatabaseRepository databaseRepository,
                                   DumperSecurityService dumperSecurityService) {
        this.databaseRepository = databaseRepository;
        this.dumperSecurityService = dumperSecurityService;
    }

    public TableContentPayload tableContent(TableContentInput input) {
        if (!dumperSecurityService.isLoggedIn()) {
            // TODO
            return new TableContentPayload(new SecurityError(SecurityErrorCode.UNAUTHORIZED, "UNAUTHORIZED"));
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

            Schema.Table table = schema.getTable(input.getTableName());

            if (table == null) {
                // TODO
                throw new UnsupportedOperationException("Table not found");
            }

            StringBuilder sql = new StringBuilder("SELECT * FROM ")
                    .append("\"")
                    .append(input.getTableName())
                    .append("\"")
                    .append(" WHERE ");

            for (int i = 0; i < input.getFilters().size(); i++) {
                TableContentFilterInput filterInput = input.getFilters().get(i);

                sql
                        .append("(")
                        .append("\"")
                        .append(filterInput.getField())
                        .append("\"")
                        .append(" = ")
                        .append("'")
                        .append(filterInput.getValue())
                        .append("'")
                        .append(")");

                if (!((i + 1) == input.getFilters().size())) {
                    sql.append(" AND ");
                }
            }

            sql.append(" LIMIT 5");

            PreparedStatement preparedStatement = connection.prepareStatement(sql.toString());

            ResultSet rs = preparedStatement.executeQuery();

            List<TableRowGraph> rows = new ArrayList<>();

            while (rs.next()) {
                List<FieldValueGraph> primaryKey = table.getPrimaryKeys().stream()
                        .map(it -> {
                            try {
                                return new FieldValueGraph(it.getName(), rs.getString(it.getName()));
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .collect(Collectors.toList());

                List<String> values = new ArrayList<>();

                for (Schema.Column schemaColumn : table.getColumns()) {
                    values.add(rs.getString(schemaColumn.getName()));
                }

                rows.add(new TableRowGraph(primaryKey, values));
            }

            List<TableColumnGraph> columns = table.getColumns().stream()
                    .map(it -> new TableColumnGraph(it.getName(), it.getType()))
                    .collect(Collectors.toList());

            TableContentPayloadDataGraph data = new TableContentPayloadDataGraph(rows, columns);

            return new TableContentPayload(data);
        } catch (SQLException | IOException e) {
            // TODO
            throw new UnsupportedOperationException(e);
        }
    }

}
