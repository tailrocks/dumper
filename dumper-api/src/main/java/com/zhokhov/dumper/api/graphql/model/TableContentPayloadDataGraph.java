package com.zhokhov.dumper.api.graphql.model;

import java.util.List;

public class TableContentPayloadDataGraph {

    private final List<TableRowGraph> rows;
    private final List<TableColumnGraph> columns;

    public TableContentPayloadDataGraph(List<TableRowGraph> rows, List<TableColumnGraph> columns) {
        this.rows = rows;
        this.columns = columns;
    }

    public List<TableRowGraph> rows() {
        return rows;
    }

    public List<TableColumnGraph> columns() {
        return columns;
    }

}
