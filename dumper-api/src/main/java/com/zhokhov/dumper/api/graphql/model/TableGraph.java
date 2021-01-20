package com.zhokhov.dumper.api.graphql.model;

import java.util.List;

public class TableGraph {

    private final String name;
    private final List<TableColumnGraph> columns;

    public TableGraph(String name, List<TableColumnGraph> columns) {
        this.name = name;
        this.columns = columns;
    }

    public String name() {
        return name;
    }

    public List<TableColumnGraph> columns() {
        return columns;
    }

}
