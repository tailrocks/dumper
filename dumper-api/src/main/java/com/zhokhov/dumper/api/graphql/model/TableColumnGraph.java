package com.zhokhov.dumper.api.graphql.model;

public class TableColumnGraph {

    private final String name;
    private final String kind;

    public TableColumnGraph(String name, String kind) {
        this.name = name;
        this.kind = kind;
    }

    public String name() {
        return name;
    }

    public String kind() {
        return kind;
    }

}
