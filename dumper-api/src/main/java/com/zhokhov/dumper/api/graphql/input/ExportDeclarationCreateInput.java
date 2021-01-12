package com.zhokhov.dumper.api.graphql.input;

import com.zhokhov.dumper.data.jooq.enums.ExportDeclarationDestination;

import java.util.List;

public class ExportDeclarationCreateInput {

    private String description;
    private ExportDeclarationDestination destination;
    private String sourceDatabaseName;
    private String targetDatabaseName;
    private List<ExportItemInput> items;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ExportDeclarationDestination getDestination() {
        return destination;
    }

    public void setDestination(ExportDeclarationDestination destination) {
        this.destination = destination;
    }

    public String getSourceDatabaseName() {
        return sourceDatabaseName;
    }

    public void setSourceDatabaseName(String sourceDatabaseName) {
        this.sourceDatabaseName = sourceDatabaseName;
    }

    public String getTargetDatabaseName() {
        return targetDatabaseName;
    }

    public void setTargetDatabaseName(String targetDatabaseName) {
        this.targetDatabaseName = targetDatabaseName;
    }

    public List<ExportItemInput> getItems() {
        return items;
    }

    public void setItems(List<ExportItemInput> items) {
        this.items = items;
    }

}
