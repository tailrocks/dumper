package com.zhokhov.dumper.data.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhokhov.dumper.data.jooq.enums.ExportItemReason;
import com.zhokhov.dumper.data.jooq.tables.records.ExportItemRecord;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.micronaut.transaction.annotation.ReadOnly;
import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.transaction.Transactional;
import java.util.Map;
import java.util.Optional;

import static com.zhokhov.dumper.data.jooq.tables.ExportItem.EXPORT_ITEM;
import static com.zhokhov.jambalaya.checks.Preconditions.checkNotBlank;
import static com.zhokhov.jambalaya.checks.Preconditions.checkNotEmpty;
import static com.zhokhov.jambalaya.checks.Preconditions.checkNotNull;
import static com.zhokhov.jambalaya.checks.Preconditions.checkPositive;

@Singleton
public class ExportItemRepository {

    private static final Logger LOG = LoggerFactory.getLogger(ExportItemRepository.class);

    private final DSLContext dslContext;
    private final ObjectMapper objectMapper;

    public ExportItemRepository(DSLContext dslContext) {
        this.dslContext = dslContext;
        this.objectMapper = new ObjectMapper();
    }

    @ReadOnly
    public Optional<ExportItemRecord> findById(long id) {
        checkPositive(id, "id");

        return dslContext.fetchOptional(EXPORT_ITEM, EXPORT_ITEM.ID.eq(id));
    }

    @Transactional
    public ExportItemRecord create(
            long exportDeclarationId,
            @NonNull ExportItemReason reason,
            @NonNull String sourceTableName,
            @NonNull Map<String, String> sourcePrimaryKey
    ) {
        checkPositive(exportDeclarationId, "exportDeclarationId");
        checkNotNull(reason, "reason");
        checkNotBlank(sourceTableName, "sourceTableName");
        checkNotEmpty(sourcePrimaryKey, "sourcePrimaryKey");

        String json;

        try {
            json = objectMapper.writeValueAsString(sourcePrimaryKey);
        } catch (JsonProcessingException e) {
            throw new UnsupportedOperationException(e);
        }

        ExportItemRecord database = dslContext.newRecord(EXPORT_ITEM)
                .setExportDeclarationId(exportDeclarationId)
                .setReason(reason)
                .setSourceTableName(sourceTableName)
                .setSourcePrimaryKey(JSONB.valueOf(json))
                .setOverrideValues(JSONB.valueOf("{}"));

        database.store();

        LOG.info("Created {}", database.getId());

        return database;
    }

}
