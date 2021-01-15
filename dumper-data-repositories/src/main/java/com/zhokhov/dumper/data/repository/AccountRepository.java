package com.zhokhov.dumper.data.repository;

import com.zhokhov.dumper.data.jooq.tables.records.AccountRecord;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.micronaut.transaction.annotation.ReadOnly;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static com.zhokhov.dumper.data.jooq.tables.Account.ACCOUNT;
import static com.zhokhov.jambalaya.checks.Preconditions.checkNotBlank;
import static com.zhokhov.jambalaya.checks.Preconditions.checkNotNull;

@Singleton
public class AccountRepository {

    private static final Logger LOG = LoggerFactory.getLogger(AccountRepository.class);

    private final DSLContext dslContext;

    public AccountRepository(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @ReadOnly
    public Optional<AccountRecord> findById(@NonNull Long id) {
        checkNotNull(id, "id");

        return dslContext.fetchOptional(ACCOUNT, ACCOUNT.ID.eq(id));
    }

    @ReadOnly
    public Optional<AccountRecord> findByUsername(@NonNull String username) {
        checkNotBlank(username, "username");

        username = username.trim().strip().toLowerCase();

        return dslContext.fetchOptional(ACCOUNT, ACCOUNT.USERNAME.eq(username));
    }

    @ReadOnly
    public List<AccountRecord> findAllOrderByUsername() {
        return dslContext
                .selectFrom(ACCOUNT)
                .orderBy(ACCOUNT.USERNAME.asc())
                .fetch();
    }

    @ReadOnly
    public boolean existsByUsername(String username) {
        checkNotBlank(username, "username");

        username = username.trim().strip().toLowerCase();

        return dslContext
                .select(ACCOUNT.ID)
                .from(ACCOUNT)
                .where(ACCOUNT.USERNAME.eq(username))
                .limit(1)
                .fetchOptional()
                .isPresent();
    }

    @Transactional
    public AccountRecord create(@NonNull String username,
                                @NonNull String password,
                                @NonNull String email,
                                @NonNull String firstName,
                                @NonNull String lastName) {
        checkNotBlank(username, "username");
        checkNotBlank(password, "password");
        checkNotBlank(email, "email");
        checkNotBlank(firstName, "firstName");
        checkNotBlank(lastName, "lastName");

        username = username.trim().strip().toLowerCase();
        email = email.trim().strip().toLowerCase();

        AccountRecord account = dslContext.newRecord(ACCOUNT)
                .setUsername(username)
                .setPassword(password)
                .setEmail(email)
                .setFirstName(firstName)
                .setLastName(lastName);

        account.store();

        LOG.info("Created {}", account.getId());

        return account;
    }

}
