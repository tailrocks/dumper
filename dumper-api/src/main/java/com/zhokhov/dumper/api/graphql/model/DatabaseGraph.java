package com.zhokhov.dumper.api.graphql.model;

import com.zhokhov.dumper.data.jooq.tables.records.DatabaseRecord;
import com.zhokhov.jambalaya.graphql.jooq.model.AbstractPersistentEntity;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.List;

public class DatabaseGraph extends AbstractPersistentEntity<Long, DatabaseRecord> {

    public DatabaseGraph(@NonNull DatabaseRecord entity) {
        super(entity);
    }

    public String name() {
        throw new UnsupportedOperationException();
    }

    public String host() {
        throw new UnsupportedOperationException();
    }

    public Integer port() {
        throw new UnsupportedOperationException();
    }

    public String username() {
        throw new UnsupportedOperationException();
    }

    public String dbname() {
        throw new UnsupportedOperationException();
    }

    public String environment() {
        throw new UnsupportedOperationException();
    }

    public String description() {
        throw new UnsupportedOperationException();
    }

    public List<DatabaseGraph> subDatabases() {
        throw new UnsupportedOperationException();
    }

}
