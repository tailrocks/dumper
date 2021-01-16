package com.zhokhov.dumper.data.repository;

import com.zhokhov.dumper.data.jooq.tables.records.DatabaseRecord;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.micronaut.transaction.annotation.ReadOnly;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static com.zhokhov.dumper.data.jooq.tables.Database.DATABASE;
import static com.zhokhov.jambalaya.checks.Preconditions.checkNotBlank;
import static com.zhokhov.jambalaya.checks.Preconditions.checkNotEmpty;
import static com.zhokhov.jambalaya.checks.Preconditions.checkPositive;

@Singleton
public class DatabaseRepository {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseRepository.class);

    private final DSLContext dslContext;

    public DatabaseRepository(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @ReadOnly
    public Optional<DatabaseRecord> findById(long id) {
        checkPositive(id, "id");

        return dslContext.fetchOptional(DATABASE, DATABASE.ID.eq(id));
    }

    @ReadOnly
    public Optional<DatabaseRecord> findByName(@NonNull String name) {
        checkNotBlank(name, "name");

        return dslContext.fetchOptional(DATABASE, DATABASE.NAME.eq(name));
    }

    @ReadOnly
    public List<DatabaseRecord> findAllByMainDatabaseId(List<Long> mainDatabaseId) {
        checkNotEmpty(mainDatabaseId, "mainDatabaseId");

        return dslContext
                .selectFrom(DATABASE)
                .where(DATABASE.MAIN_DATABASE_ID.in(mainDatabaseId))
                .fetch();
    }

    @Transactional
    public DatabaseRecord create(
            @NonNull String name,
            @NonNull String host,
            int port,
            @NonNull String username,
            @Nullable String password,
            @NonNull String dbname,
            @NonNull String environment,
            @NonNull String description,
            @Nullable Long mainDatabaseId
    ) {
        checkNotBlank(name, "name");
        checkNotBlank(host, "host");
        checkPositive(port, "port");
        checkNotBlank(username, "username");
        checkNotBlank(dbname, "dbname");
        checkNotBlank(environment, "environment");
        checkNotBlank(description, "description");

        name = name.trim().strip().toLowerCase();

        DatabaseRecord database = dslContext.newRecord(DATABASE)
                .setName(name)
                .setHost(host)
                .setPort(port)
                .setUsername(username)
                .setPassword(password)
                .setDbname(dbname)
                .setEnvironment(environment)
                .setDescription(description)
                .setMainDatabaseId(mainDatabaseId);

        database.store();

        LOG.info("Created {}", database.getId());

        return database;
    }

}
