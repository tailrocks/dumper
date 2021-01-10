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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhokhov.dumper.share.TableStructure;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.zhokhov.jambalaya.checks.Preconditions.checkNotNull;

public final class Importer {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private Importer() {
    }

    public static void insertResource(@NonNull Connection connection, @NonNull String dir, @NonNull String tableName,
                                      @NonNull String id) {
        checkNotNull(dir, "dir");
        checkNotNull(tableName, "tableName");
        checkNotNull(id, "id");

        while (dir.endsWith("/")) {
            dir = dir.substring(0, dir.length() - 1);
        }

        Set<String> processedDocuments = new HashSet<>();

        insertItem(processedDocuments, connection, dir, tableName, id, false);
    }

    public static void upsertResource(@NonNull Connection connection, @NonNull String dir, @NonNull String tableName,
                                      @NonNull String id) {
        checkNotNull(dir, "dir");
        checkNotNull(tableName, "tableName");
        checkNotNull(id, "id");

        while (dir.endsWith("/")) {
            dir = dir.substring(0, dir.length() - 1);
        }

        Set<String> processedDocuments = new HashSet<>();

        insertItem(processedDocuments, connection, dir, tableName, id, true);
    }

    private static void insertItem(Set<String> processedDocuments, Connection connection, String dir,
                                   String tableName, String id, boolean upsert) {
        String documentResource = dir + "/" + tableName + "/" + id + ".json";
        String tableStructureResource = dir + "/" + tableName + ".json";

        if (processedDocuments.contains(documentResource)) {
            // skip processing
            return;
        }

        Map<String, Object> document;

        try (InputStream inputStream = Importer.class.getResourceAsStream(documentResource)) {
            if (inputStream == null) {
                throw new RuntimeException("Resource not found: " + documentResource);
            }

            document = OBJECT_MAPPER.readValue(inputStream, LinkedHashMap.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        TableStructure tableStructure;

        try (InputStream inputStream = Importer.class.getResourceAsStream(tableStructureResource)) {
            if (inputStream == null) {
                throw new RuntimeException("Resource not found: " + tableStructureResource);
            }

            tableStructure = OBJECT_MAPPER.readValue(inputStream, TableStructure.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        Map<String, TableStructure.Column> columnsByName = new HashMap<>();

        for (int i = 0; i < tableStructure.getColumns().size(); i++) {
            TableStructure.Column column = tableStructure.getColumns().get(i);

            columnsByName.put(column.getName(), column);

            Object value = document.get(column.getName());

            if (value != null && !column.getForeignKeys().isEmpty()) {
                for (TableStructure.ForeignKey foreignKey : column.getForeignKeys()) {
                    insertItem(processedDocuments, connection, dir, foreignKey.getReferenceTableName(),
                            value.toString(), upsert);
                }
            }
        }


        StringBuilder insertQuery = new StringBuilder("INSERT INTO ")
                .append("\"")
                .append(tableStructure.getTableName())
                .append("\"")
                .append(" ")
                .append("(");

        List<String> columnNames = new ArrayList<>(document.keySet());

        for (int i = 0; i < columnNames.size(); i++) {
            String columnName = columnNames.get(i);
            boolean hasNext = (columnNames.size() - 1) > i;

            insertQuery.append("\"").append(columnName).append("\"");
            if (hasNext) {
                insertQuery.append(", ");
            }
        }

        insertQuery.append(") VALUES (");

        for (int i = 0; i < columnNames.size(); i++) {
            String columnName = columnNames.get(i);
            boolean hasNext = (columnNames.size() - 1) > i;

            TableStructure.Column column = columnsByName.get(columnName);

            if (column == null) {
                throw new IllegalStateException("Column not found");
            }

            Object value = document.get(columnName);

            setParameter(insertQuery, value, column);

            if (hasNext) {
                insertQuery.append(", ");
            }
        }

        insertQuery.append(")");

        if (upsert) {
            insertQuery.append(" ON CONFLICT ON CONSTRAINT ");
            insertQuery.append("\"").append(tableStructure.getPrimaryKeyConstraint()).append("\"");

            insertQuery.append(" DO UPDATE SET ");

            for (int i = 0; i < columnNames.size(); i++) {
                String columnName = columnNames.get(i);
                boolean hasNext = (columnNames.size() - 1) > i;

                insertQuery
                        .append("\"").append(columnName).append("\"")
                        .append(" = ")
                        .append("EXCLUDED.").append("\"").append(columnName).append("\"");

                if (hasNext) {
                    insertQuery.append(", ");
                }
            }
        }

        try {
            Statement statement = connection.createStatement();

            statement.executeUpdate(insertQuery.toString());

            processedDocuments.add(documentResource);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setParameter(StringBuilder insertQuery, Object value, TableStructure.Column column) {
        if (value == null) {
            insertQuery.append("NULL");
        } else {
            String type = column.getType();

            if (column.isUserDefinedType()) {
                if (value instanceof String) {
                    insertQuery.append("'").append(value).append("'");
                } else {
                    throw new UnsupportedOperationException(type + ", unknown class: " + value.getClass());
                }
            } else {
                if (type.startsWith("_")) {
                    // process array
                    if (value instanceof String) {
                        insertQuery.append("'").append(value).append("'");
                    } else {
                        throw new UnsupportedOperationException(type + ", unknown class: " + value.getClass());
                    }
                } else {
                    switch (type) {
                        case "int4":
                            if (value instanceof Integer) {
                                insertQuery.append(value);
                            } else {
                                throw new UnsupportedOperationException(type + ", unknown class: " + value.getClass());
                            }
                            break;
                        case "int8":
                            if (value instanceof Integer || value instanceof Long) {
                                insertQuery.append(value);
                            } else {
                                throw new UnsupportedOperationException(type + ", unknown class: " + value.getClass());
                            }
                            break;
                        case "timestamp":
                            if (value instanceof String) {
                                String stringValue = (String) value;

                                insertQuery.append("'").append(stringValue).append("'");
                            } else {
                                throw new UnsupportedOperationException(type + ", unknown class: " + value.getClass());
                            }
                            break;
                        case "bool":
                            if (value instanceof Boolean) {
                                if ((Boolean) value) {
                                    insertQuery.append("'t'");
                                } else {
                                    insertQuery.append("'f'");
                                }
                            } else {
                                throw new UnsupportedOperationException(type + ", unknown class: " + value.getClass());
                            }
                            break;
                        case "varchar":
                        case "text":
                            if (value instanceof String) {
                                String encodedString = ((String) value).replace("'", "''");

                                insertQuery.append("'").append(encodedString).append("'");
                            } else {
                                throw new UnsupportedOperationException(type + ", unknown class: " + value.getClass());
                            }
                            break;
                        default:
                            throw new UnsupportedOperationException("Unknown type: " + type);
                    }
                }
            }
        }
    }

}
