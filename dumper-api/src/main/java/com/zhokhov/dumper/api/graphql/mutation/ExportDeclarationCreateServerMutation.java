package com.zhokhov.dumper.api.graphql.mutation;

import com.zhokhov.dumper.api.graphql.error.SecurityError;
import com.zhokhov.dumper.api.graphql.error.code.SecurityErrorCode;
import com.zhokhov.dumper.api.graphql.input.ExportDeclarationCreateInput;
import com.zhokhov.dumper.api.graphql.input.ExportItemInput;
import com.zhokhov.dumper.api.graphql.input.FieldValueInput;
import com.zhokhov.dumper.api.graphql.model.ExportDeclarationGraph;
import com.zhokhov.dumper.api.graphql.payload.ExportDeclarationCreatePayload;
import com.zhokhov.dumper.api.security.DumperSecurityService;
import com.zhokhov.dumper.data.jooq.enums.ExportItemReason;
import com.zhokhov.dumper.data.jooq.tables.records.AccountRecord;
import com.zhokhov.dumper.data.jooq.tables.records.DatabaseRecord;
import com.zhokhov.dumper.data.jooq.tables.records.ExportDeclarationRecord;
import com.zhokhov.dumper.data.repository.DatabaseRepository;
import com.zhokhov.dumper.data.repository.ExportDeclarationRepository;
import com.zhokhov.dumper.data.repository.ExportItemRepository;
import graphql.kickstart.tools.GraphQLMutationResolver;

import javax.inject.Singleton;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Singleton
public class ExportDeclarationCreateServerMutation implements GraphQLMutationResolver {

    private final DatabaseRepository databaseRepository;
    private final ExportDeclarationRepository exportDeclarationRepository;
    private final ExportItemRepository exportItemRepository;
    private final DumperSecurityService dumperSecurityService;

    public ExportDeclarationCreateServerMutation(
            DatabaseRepository databaseRepository,
            ExportDeclarationRepository exportDeclarationRepository,
            ExportItemRepository exportItemRepository,
            DumperSecurityService dumperSecurityService
    ) {
        this.databaseRepository = databaseRepository;
        this.exportDeclarationRepository = exportDeclarationRepository;
        this.exportItemRepository = exportItemRepository;
        this.dumperSecurityService = dumperSecurityService;
    }

    public ExportDeclarationCreatePayload exportDeclarationCreate(ExportDeclarationCreateInput input) {
        if (!dumperSecurityService.isLoggedIn()) {
            // TODO
            return new ExportDeclarationCreatePayload(new SecurityError(SecurityErrorCode.UNAUTHORIZED, "UNAUTHORIZED"));
        }

        Optional<DatabaseRecord> sourceDatabase = databaseRepository.findByName(input.getSourceDatabaseName());

        if (sourceDatabase.isEmpty()) {
            // TODO
            throw new UnsupportedOperationException("Source database not found");
        }

        Optional<DatabaseRecord> targetDatabase =
                input.getTargetDatabaseName() != null ? databaseRepository.findByName(input.getTargetDatabaseName())
                        : Optional.empty();

        ExportDeclarationRecord exportDeclarationRecord = create(input, sourceDatabase.get(), targetDatabase);

        return new ExportDeclarationCreatePayload(new ExportDeclarationGraph(exportDeclarationRecord));
    }

    @Transactional
    protected ExportDeclarationRecord create(
            ExportDeclarationCreateInput input,
            DatabaseRecord sourceDatabase,
            Optional<DatabaseRecord> targetDatabase
    ) {
        AccountRecord accountRecord = dumperSecurityService.getCurrentUser().orElseThrow();

        ExportDeclarationRecord exportDeclarationRecord = exportDeclarationRepository.create(
                accountRecord.getId(),
                input.getDescription(),
                input.getDestination(),
                sourceDatabase.getId(),
                targetDatabase.map(DatabaseRecord::getId).orElse(null)
        );

        for (ExportItemInput exportItemInput : input.getItems()) {
            Map<String, String> sourcePrimaryKey = new HashMap<>();

            for (FieldValueInput fieldValueInput : exportItemInput.getSourcePrimaryKey()) {
                sourcePrimaryKey.put(fieldValueInput.getField(), fieldValueInput.getValue());
            }

            exportItemRepository.create(
                    exportDeclarationRecord.getId(),
                    ExportItemReason.REQUEST,
                    exportItemInput.getSourceTableName(),
                    sourcePrimaryKey
            );
        }

        return exportDeclarationRecord;
    }

}
