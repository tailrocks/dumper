package com.zhokhov.dumper.data.repository;

import com.zhokhov.dumper.data.jooq.enums.ExportDeclarationDestination;
import com.zhokhov.dumper.data.jooq.enums.ExportDeclarationStatus;
import com.zhokhov.dumper.data.jooq.tables.records.ExportDeclarationRecord;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.micronaut.transaction.annotation.ReadOnly;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.zhokhov.dumper.data.jooq.tables.ExportDeclaration.EXPORT_DECLARATION;
import static com.zhokhov.jambalaya.checks.Preconditions.checkNotBlank;
import static com.zhokhov.jambalaya.checks.Preconditions.checkNotNull;
import static com.zhokhov.jambalaya.checks.Preconditions.checkPositive;

@Singleton
public class ExportDeclarationRepository {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseRepository.class);

    private final DSLContext dslContext;

    public ExportDeclarationRepository(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @ReadOnly
    public Optional<ExportDeclarationRecord> findById(long id) {
        checkPositive(id, "id");

        return dslContext.fetchOptional(EXPORT_DECLARATION, EXPORT_DECLARATION.ID.eq(id));
    }

    @ReadOnly
    public List<ExportDeclarationRecord> findAll() {
        return dslContext.fetch(EXPORT_DECLARATION);
    }

    @Transactional
    public ExportDeclarationRecord create(
            long exporterAccountId,
            @NonNull String description,
            @NonNull ExportDeclarationDestination destination,
            long sourceDatabaseId,
            @Nullable Long targetDatabaseId
    ) {
        checkPositive(exporterAccountId, "exporterAccountId");
        checkNotBlank(description, "description");
        checkNotNull(destination, "destination");
        checkPositive(sourceDatabaseId, "sourceDatabaseId");
        if (targetDatabaseId != null) {
            checkPositive(targetDatabaseId, "targetDatabaseId");
        }

        ExportDeclarationRecord database = dslContext.newRecord(EXPORT_DECLARATION)
                .setDate(LocalDateTime.now())
                .setExporterAccountId(exporterAccountId)
                .setStatus(ExportDeclarationStatus.PENDING)
                .setDescription(description)
                .setDestination(destination)
                .setSourceDatabaseId(sourceDatabaseId)
                .setTargetDatabaseId(targetDatabaseId);

        database.store();

        LOG.info("Created {}", database.getId());

        return database;
    }

}
