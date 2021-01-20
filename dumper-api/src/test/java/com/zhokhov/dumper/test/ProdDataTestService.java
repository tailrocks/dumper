package com.zhokhov.dumper.test;

import io.micronaut.transaction.annotation.TransactionalAdvice;
import org.jooq.DSLContext;

import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class ProdDataTestService {

    private final DSLContext dslContext;

    public ProdDataTestService(@Named("example-prod") DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @TransactionalAdvice("example-prod")
    public void createAdminAccount() {
        String query = "INSERT INTO \"public\".\"account\" (\"id\", \"created_date\", \"last_modified_date\", \"version\", \"username\", \"email\", \"password\", \"enabled\", \"gender\", \"roles\", \"registration_date\") VALUES\n" +
                "('1', '2021-01-20 20:09:47.558574', '2021-01-20 20:09:47.558574', '1', 'admin', 'admin@admin', " +
                "'admin', 't', 'MALE', '{ROLE_ADMIN}', '2021-01-20 20:09:47.558574');";

        dslContext.execute(query);
    }

}
