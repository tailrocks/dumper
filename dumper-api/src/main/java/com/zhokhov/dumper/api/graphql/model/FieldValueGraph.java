package com.zhokhov.dumper.api.graphql.model;

public class FieldValueGraph {

    private final String field;
    private final String value;

    public FieldValueGraph(String field, String value) {
        this.field = field;
        this.value = value;
    }

    public String field() {
        return field;
    }

    public String value() {
        return value;
    }

}
