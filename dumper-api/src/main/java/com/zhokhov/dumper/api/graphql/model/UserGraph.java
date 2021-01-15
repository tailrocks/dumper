package com.zhokhov.dumper.api.graphql.model;

import com.zhokhov.dumper.data.jooq.tables.records.AccountRecord;
import com.zhokhov.jambalaya.graphql.jooq.model.AbstractPersistentEntity;
import edu.umd.cs.findbugs.annotations.NonNull;

public class UserGraph extends AbstractPersistentEntity<Long, AccountRecord> {

    public UserGraph(@NonNull AccountRecord entity) {
        super(entity);
    }

    public String username() {
        return getEntity().getUsername();
    }

    public String email() {
        return getEntity().getEmail();
    }

    public String firstName() {
        return getEntity().getFirstName();
    }

    public String lastName() {
        return getEntity().getLastName();
    }

}
