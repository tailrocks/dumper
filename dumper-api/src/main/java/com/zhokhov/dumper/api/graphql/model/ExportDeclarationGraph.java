package com.zhokhov.dumper.api.graphql.model;

import com.zhokhov.dumper.data.jooq.enums.ExportDeclarationDestination;
import com.zhokhov.dumper.data.jooq.enums.ExportDeclarationStatus;
import com.zhokhov.dumper.data.jooq.tables.records.ExportDeclarationRecord;
import com.zhokhov.jambalaya.graphql.jooq.model.AbstractPersistentEntity;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.time.LocalDateTime;

public class ExportDeclarationGraph extends AbstractPersistentEntity<Long, ExportDeclarationRecord> {

    public ExportDeclarationGraph(@NonNull ExportDeclarationRecord entity) {
        super(entity);
    }

    public LocalDateTime date() {
        throw new UnsupportedOperationException();
    }

    public UserGraph exporter() {
        throw new UnsupportedOperationException();
    }

    public ExportDeclarationStatus status() {
        throw new UnsupportedOperationException();
    }

    public String description() {
        throw new UnsupportedOperationException();
    }

    public ExportDeclarationDestination destination() {
        throw new UnsupportedOperationException();
    }

}
