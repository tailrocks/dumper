package com.zhokhov.dumper.api.graphql.model;

import com.zhokhov.dumper.data.jooq.tables.records.AccountRecord;
import com.zhokhov.jambalaya.graphql.jooq.model.AbstractPersistentEntity;
import edu.umd.cs.findbugs.annotations.NonNull;

public class UserGraph extends AbstractPersistentEntity<Long, AccountRecord> {

    public UserGraph(@NonNull AccountRecord entity) {
        super(entity);
    }

    public String username() {
        throw new UnsupportedOperationException();
    }

    public String email() {
        throw new UnsupportedOperationException();
    }

    public String firstName() {
        throw new UnsupportedOperationException();
    }

    public String lastName() {
        throw new UnsupportedOperationException();
    }

}
