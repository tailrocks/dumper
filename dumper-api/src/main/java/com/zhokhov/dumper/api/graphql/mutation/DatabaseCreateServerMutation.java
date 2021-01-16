package com.zhokhov.dumper.api.graphql.mutation;

import com.zhokhov.dumper.api.graphql.error.SecurityError;
import com.zhokhov.dumper.api.graphql.error.code.SecurityErrorCode;
import com.zhokhov.dumper.api.graphql.input.DatabaseCreateInput;
import com.zhokhov.dumper.api.graphql.model.DatabaseGraph;
import com.zhokhov.dumper.api.graphql.payload.DatabaseCreatePayload;
import com.zhokhov.dumper.api.security.DumperSecurityService;
import com.zhokhov.dumper.data.jooq.tables.records.DatabaseRecord;
import com.zhokhov.dumper.data.repository.DatabaseRepository;
import graphql.kickstart.tools.GraphQLMutationResolver;

import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class DatabaseCreateServerMutation implements GraphQLMutationResolver {

    private final DatabaseRepository databaseRepository;
    private final DumperSecurityService dumperSecurityService;

    public DatabaseCreateServerMutation(DatabaseRepository databaseRepository,
                                        DumperSecurityService dumperSecurityService) {
        this.databaseRepository = databaseRepository;
        this.dumperSecurityService = dumperSecurityService;
    }

    public DatabaseCreatePayload databaseCreate(DatabaseCreateInput input) {
        if (!dumperSecurityService.isLoggedIn()) {
            // TODO
            return new DatabaseCreatePayload(new SecurityError(SecurityErrorCode.UNAUTHORIZED, "UNAUTHORIZED"));
        }

        String environment = input.getEnvironment().toLowerCase();

        Optional<DatabaseRecord> mainDatabase = Optional.empty();

        if (input.getMainDatabaseName() != null) {
            mainDatabase = databaseRepository.findByName(input.getMainDatabaseName());
        }

        String name = mainDatabase
                .map(record -> record.getName() + "-" + environment)
                .orElseGet(() -> input.getName().toLowerCase());

        DatabaseRecord databaseRecord = databaseRepository.create(
                name,
                input.getHost(),
                input.getPort(),
                input.getUsername(),
                input.getPassword(),
                input.getDbname(),
                environment,
                input.getDescription(),
                mainDatabase.map(DatabaseRecord::getId).orElse(null)
        );

        return new DatabaseCreatePayload(new DatabaseGraph(databaseRecord));
    }

}
