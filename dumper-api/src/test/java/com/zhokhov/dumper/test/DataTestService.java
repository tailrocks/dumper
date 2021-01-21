package com.zhokhov.dumper.test;

import org.jooq.DSLContext;

import javax.inject.Singleton;
import javax.transaction.Transactional;

import static com.zhokhov.dumper.data.jooq.tables.Account.ACCOUNT;
import static com.zhokhov.dumper.data.jooq.tables.Database.DATABASE;
import static com.zhokhov.dumper.data.jooq.tables.ExportDeclaration.EXPORT_DECLARATION;
import static com.zhokhov.dumper.data.jooq.tables.ExportItem.EXPORT_ITEM;

@Singleton
public class DataTestService {

    private final DSLContext dslContext;

    public DataTestService(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Transactional
    public void clean() {
        dslContext.deleteFrom(EXPORT_ITEM).execute();
        dslContext.deleteFrom(EXPORT_DECLARATION).execute();
        dslContext.deleteFrom(ACCOUNT).execute();
        dslContext.deleteFrom(DATABASE).execute();
    }

}
