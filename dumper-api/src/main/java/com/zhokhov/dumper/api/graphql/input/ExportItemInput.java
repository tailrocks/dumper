package com.zhokhov.dumper.api.graphql.input;

import java.util.List;

public class ExportItemInput {

    private String sourceTableName;
    private List<FieldValueInput> sourcePrimaryKey;
    private List<FieldValueInput> overrideValues;

    public String getSourceTableName() {
        return sourceTableName;
    }

    public void setSourceTableName(String sourceTableName) {
        this.sourceTableName = sourceTableName;
    }

    public List<FieldValueInput> getSourcePrimaryKey() {
        return sourcePrimaryKey;
    }

    public void setSourcePrimaryKey(List<FieldValueInput> sourcePrimaryKey) {
        this.sourcePrimaryKey = sourcePrimaryKey;
    }

    public List<FieldValueInput> getOverrideValues() {
        return overrideValues;
    }

    public void setOverrideValues(List<FieldValueInput> overrideValues) {
        this.overrideValues = overrideValues;
    }

}
