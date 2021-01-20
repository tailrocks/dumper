package com.zhokhov.dumper.api.graphql.model;

import java.util.List;

public class TableRowGraph {

    private final List<FieldValueGraph> primaryKey;
    private final List<String> values;

    public TableRowGraph(List<FieldValueGraph> primaryKey, List<String> values) {
        this.primaryKey = primaryKey;
        this.values = values;
    }

    public List<FieldValueGraph> primaryKey() {
        return primaryKey;
    }

    public List<String> values() {
        return values;
    }

}
