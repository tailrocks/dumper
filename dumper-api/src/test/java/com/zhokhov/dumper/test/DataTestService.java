package com.zhokhov.dumper.test;

import org.jooq.DSLContext;

import javax.inject.Singleton;
import javax.transaction.Transactional;

import static com.zhokhov.dumper.data.jooq.tables.Account.ACCOUNT;

@Singleton
public class DataTestService {

    private final DSLContext dslContext;

    public DataTestService(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Transactional
    public void clean() {
        dslContext.deleteFrom(ACCOUNT).execute();
    }

}
