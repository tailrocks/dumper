package com.zhokhov.dumper.api.graphql.model;

import com.zhokhov.dumper.data.jooq.tables.records.DatabaseRecord;
import com.zhokhov.jambalaya.graphql.jooq.model.AbstractPersistentEntity;
import edu.umd.cs.findbugs.annotations.NonNull;

public class DatabaseGraph extends AbstractPersistentEntity<Long, DatabaseRecord> {

    public DatabaseGraph(@NonNull DatabaseRecord entity) {
        super(entity);
    }

    public String name() {
        return getEntity().getName();
    }

    public String host() {
        return getEntity().getHost();
    }

    public Integer port() {
        return getEntity().getPort();
    }

    public String username() {
        return getEntity().getUsername();
    }

    public String dbname() {
        return getEntity().getDbname();
    }

    public String environment() {
        return getEntity().getEnvironment();
    }

    public String description() {
        return getEntity().getDescription();
    }

}
