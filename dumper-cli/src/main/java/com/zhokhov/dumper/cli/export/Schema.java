package com.zhokhov.dumper.cli.export;

import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.zhokhov.jambalaya.checks.Preconditions.checkNotBlank;

public final class Schema {

    private final Map<String, Table> tables = new LinkedHashMap<>();

    protected Schema() {
    }

    public Table getTable(@NonNull String name) {
        checkNotBlank(name, "name");

        return tables.get(name);
    }

    protected void addColumn(@NonNull String tableName, @NonNull String columnName, @NonNull String type, boolean userDefinedType) {
        checkNotBlank(tableName, "tableName");
        checkNotBlank(columnName, "columnName");
        checkNotBlank(type, "type");

        tables.putIfAbsent(tableName, new Table(tableName));
        Table table = tables.get(tableName);
        table.addColumn(columnName, type, userDefinedType);
    }

    protected void setPrimaryKey(@NonNull String tableName, @NonNull String columnName, @NonNull String constraintName) {
        checkNotBlank(tableName, "tableName");
        checkNotBlank(columnName, "columnName");
        checkNotBlank(constraintName, "constraintName");

        Table table = tables.get(tableName);
        if (table == null) {
            throw new IllegalStateException("table is null");
        }
        table.setPrimaryKey(columnName);
        table.setPrimaryKeyConstraint(constraintName);
    }

    protected void addForeignKey(@NonNull String tableName, @NonNull String columnName,
                                 @NonNull String foreignTableName, @NonNull String foreignColumnName) {
        Table table = tables.get(tableName);

        if (table == null) {
            throw new IllegalStateException("table is null");
        }

        Table foreignTable = tables.get(foreignTableName);

        if (foreignTable == null) {
            throw new IllegalStateException("foreignTable is null");
        }

        Column column = table.columns.get(columnName);

        if (column == null) {
            throw new IllegalStateException("column is null");
        }

        Column foreignColumn = foreignTable.columns.get(foreignColumnName);

        if (foreignColumn == null) {
            throw new IllegalStateException("foreignColumn is null");
        }

        column.addForeignKey(foreignColumn);
    }

    public static class Table {

        private final String name;
        private final Map<String, Column> columns = new LinkedHashMap<>();
        private final Map<String, Column> primaryKeys = new LinkedHashMap<>();
        private String primaryKeyConstraint = null;

        private Table(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public List<Column> getColumns() {
            return new ArrayList<>(columns.values());
        }

        public Column getColumn(@NonNull String columnName) {
            return columns.get(columnName);
        }

        public List<Column> getPrimaryKeys() {
            return List.copyOf(primaryKeys.values());
        }

        public String getPrimaryKeyConstraint() {
            return primaryKeyConstraint;
        }

        private void addColumn(@NonNull String columnName, @NonNull String type, boolean userDefinedType) {
            columns.putIfAbsent(columnName, new Column(this, columnName, type, userDefinedType));
        }

        private void setPrimaryKey(@NonNull String columnName) {
            Column column = columns.get(columnName);

            if (column == null) {
                throw new IllegalStateException("column is null");
            }

            primaryKeys.putIfAbsent(column.getName(), column);
        }

        private void setPrimaryKeyConstraint(String primaryKeyConstraint) {
            this.primaryKeyConstraint = primaryKeyConstraint;
        }

    }

    public static class Column {

        private final Table table;
        private final String name;
        private final String type;
        private final boolean userDefinedType;
        private final List<Column> foreignKeys = new ArrayList<>();

        private Column(Table table, String name, String type, boolean userDefinedType) {
            this.table = table;
            this.name = name;
            this.type = type;
            this.userDefinedType = userDefinedType;
        }

        public Table getTable() {
            return table;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public boolean isUserDefinedType() {
            return userDefinedType;
        }

        public List<Column> getForeignKeys() {
            return foreignKeys;
        }

        private void addForeignKey(@NonNull Column foreignKey) {
            foreignKeys.add(foreignKey);
        }

    }

}
