package com.zhokhov.dumper.api.graphql.input;

import java.util.List;

public class TableContentInput {

    private String databaseName;
    private String tableName;
    private List<TableContentFilterInput> filters;

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<TableContentFilterInput> getFilters() {
        return filters;
    }

    public void setFilters(List<TableContentFilterInput> filters) {
        this.filters = filters;
    }

}
