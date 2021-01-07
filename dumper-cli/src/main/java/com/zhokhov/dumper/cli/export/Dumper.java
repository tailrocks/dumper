package com.zhokhov.dumper.cli.export;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.zhokhov.dumper.share.TableStructure;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.zhokhov.jambalaya.checks.Preconditions.checkNotNull;

public final class Dumper {

    private static final Logger LOG = LoggerFactory.getLogger(Dumper.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")
            .withZone(ZoneOffset.UTC);

    private Dumper() {
    }

    public static ExportReport exportToDir(@NonNull String dataSourceUrl, @NonNull String dataSourceUsername,
                                           @Nullable String dataSourcePassword, @NonNull ExportRequest exportRequest,
                                           @NonNull Path destinationDir) throws IOException {
        checkNotNull(dataSourceUrl, "dataSourceUrl");
        checkNotNull(dataSourceUsername, "dataSourceUsername");
        checkNotNull(exportRequest, "exportRequest");
        checkNotNull(destinationDir, "destinationDir");

        if (exportRequest.isEmpty()) {
            throw new IllegalStateException("exportRequest is empty");
        }

        Files.createDirectories(destinationDir);

        destinationDir = destinationDir.toAbsolutePath();

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            System.out.println("Connecting to " + dataSourceUrl);

            try (Connection connection = DriverManager.getConnection(dataSourceUrl, dataSourceUsername, dataSourcePassword)) {
                Schema schema = SchemaReader.read(connection);

                ExportReport exportReport = new ExportReport();

                for (String exportTableName : exportRequest.getExportTables()) {
                    List<String> exportIds = exportRequest.getExportIds(exportTableName);

                    exportItems(connection, schema, exportReport, exportTableName, exportIds, destinationDir, true);
                }

                while (!exportReport.isEmptyPendingLinkedItems()) {
                    // TODO too much iterations

                    for (String linkedTableName : exportReport.getPendingLinkedTables()) {
                        List<String> linkedIds = exportReport.getPendingLinkedIds(linkedTableName);

                        if (!linkedIds.isEmpty()) {
                            exportItems(connection, schema, exportReport, linkedTableName, linkedIds, destinationDir, false);
                        }
                    }
                }

                Set<String> tables = exportReport.getProcessedTables();

                for (String tableName : tables) {
                    exportTableSchema(schema.getTable(tableName), destinationDir);
                }

                return exportReport;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void exportItems(
            Connection connection,
            Schema schema,
            ExportReport exportReport,
            String exportTableName,
            List<String> ids,
            Path destinationDir,
            boolean requested
    ) throws SQLException, IOException {
        Schema.Table table = schema.getTable(exportTableName);

        if (table == null) {
            throw new RuntimeException("Table not found: " + exportTableName);
        }

        if (table.getPrimaryKeys().size() == 0) {
            throw new UnsupportedOperationException("Tables with zero primary keys are not supported yet");
        }

        if (table.getPrimaryKeys().size() > 1) {
            throw new UnsupportedOperationException("Tables with multiple primary keys are not supported " +
                    "yet");
        }

        String[] onlyNotProcessedIds = ids.stream()
                .filter(id -> !exportReport.isProcessed(exportTableName, id))
                .toArray(String[]::new);

        if (onlyNotProcessedIds.length == 0) {
            // nothing to do
            return;
        }

        String primaryKey = table.getPrimaryKeys().get(0).getName();

        String sql = "SELECT * FROM " +
                "\"" +
                table.getName() +
                "\"" +
                " WHERE " +
                "\"" +
                primaryKey +
                "\"" +
                " = " +
                "ANY(?)";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        Array array = connection.createArrayOf(
                table.getPrimaryKeys().get(0).getType(),
                onlyNotProcessedIds
        );
        preparedStatement.setArray(1, array);
        ResultSet rs = preparedStatement.executeQuery();

        processResultSet(rs, destinationDir, table, exportReport, requested);
    }

    private static void processResultSet(
            ResultSet rs,
            Path destinationDir,
            Schema.Table tableSchema,
            ExportReport exportReport,
            boolean requested
    ) throws SQLException, IOException {
        String primaryKeyColumn = tableSchema.getPrimaryKeys().get(0).getName();

        while (rs.next()) {
            Map<String, Object> exportDocument = new LinkedHashMap<>();

            int columns = rs.getMetaData().getColumnCount();

            for (int i = 1; i <= columns; i++) {
                String columnName = rs.getMetaData().getColumnName(i);
                String typeName = rs.getMetaData().getColumnTypeName(i);

                Object value = null;

                String stringValue = rs.getString(i);

                switch (typeName) {
                    case "bool":
                        String boolValue = rs.getString(i);
                        if (boolValue != null) {
                            if (Objects.equals("t", boolValue)) {
                                value = true;
                            } else {
                                value = false;
                            }
                        }
                        break;
                    case "int4":
                        if (stringValue != null) {
                            value = rs.getInt(i);
                        }
                        break;
                    case "bigserial":
                    case "int8":
                        if (stringValue != null) {
                            value = rs.getLong(i);
                        }
                        break;
                    case "timestamp":
                        Timestamp timestamp = rs.getTimestamp(i);

                        if (timestamp != null) {
                            value = DATE_TIME_FORMATTER.format(timestamp.toInstant());
                        }
                        break;
                    default:
                        value = rs.getString(i);
                        break;
                }

                exportDocument.put(columnName, value);

                if (value != null) {
                    List<Schema.Column> foreignKeys = tableSchema.getColumn(columnName).getForeignKeys();

                    if (!foreignKeys.isEmpty()) {
                        for (Schema.Column foreignKeyColumn : foreignKeys) {
                            exportReport.addPendingLinkedItem(foreignKeyColumn.getTable().getName(), value.toString());
                        }
                    }
                }
            }

            try {
                Files.createDirectories(destinationDir.resolve(Path.of(tableSchema.getName())));

                File outputFile = destinationDir.resolve(
                        Path.of(tableSchema.getName(), exportDocument.get(primaryKeyColumn) + ".json")
                ).toFile();

                System.out.println("Exporting data: " + outputFile);

                OBJECT_MAPPER.writeValue(outputFile, exportDocument);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            if (requested) {
                exportReport.addRequestedItem(tableSchema.getName(), exportDocument.get(primaryKeyColumn).toString());
            } else {
                exportReport.addLinkedItem(tableSchema.getName(), exportDocument.get(primaryKeyColumn).toString());
            }
        }
    }

    private static void exportTableSchema(Schema.Table tableSchema, Path destinationDir) throws IOException {
        checkNotNull(tableSchema, "table");

        List<String> primaryKeys = tableSchema.getPrimaryKeys().stream()
                .map(Schema.Column::getName)
                .collect(Collectors.toList());

        List<TableStructure.Column> columns = tableSchema.getColumns().stream()
                .map(it -> {
                    List<TableStructure.ForeignKey> foreignKeys = it.getForeignKeys().stream()
                            .map(fk -> new TableStructure.ForeignKey(fk.getTable().getName(), fk.getName()))
                            .collect(Collectors.toList());

                    return new TableStructure.Column(it.getName(), it.getType(), it.isUserDefinedType(), foreignKeys);
                })
                .collect(Collectors.toList());

        TableStructure tableStructure = new TableStructure(
                tableSchema.getName(),
                tableSchema.getPrimaryKeyConstraint(),
                primaryKeys,
                columns
        );

        Files.createDirectories(destinationDir.resolve(Path.of(tableSchema.getName())));

        File outputFile = destinationDir.resolve(tableSchema.getName() + ".json").toFile();

        System.out.println("Exporting schema: " + outputFile);

        OBJECT_MAPPER.writeValue(outputFile, tableStructure);
    }

}
