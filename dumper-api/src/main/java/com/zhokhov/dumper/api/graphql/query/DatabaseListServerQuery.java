package com.zhokhov.dumper.api.graphql.query;

import com.zhokhov.dumper.api.graphql.error.SecurityError;
import com.zhokhov.dumper.api.graphql.error.code.SecurityErrorCode;
import com.zhokhov.dumper.api.graphql.model.DatabaseGraph;
import com.zhokhov.dumper.api.graphql.payload.DatabaseListPayload;
import com.zhokhov.dumper.api.security.DumperSecurityService;
import com.zhokhov.dumper.data.jooq.tables.records.DatabaseRecord;
import com.zhokhov.dumper.data.repository.DatabaseRepository;
import graphql.kickstart.tools.GraphQLQueryResolver;

import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class DatabaseListServerQuery implements GraphQLQueryResolver {

    private final DatabaseRepository databaseRepository;
    private final DumperSecurityService dumperSecurityService;

    public DatabaseListServerQuery(DatabaseRepository databaseRepository, DumperSecurityService dumperSecurityService) {
        this.databaseRepository = databaseRepository;
        this.dumperSecurityService = dumperSecurityService;
    }

    public DatabaseListPayload databaseList() {
        if (!dumperSecurityService.isLoggedIn()) {
            // TODO
            return new DatabaseListPayload(new SecurityError(SecurityErrorCode.UNAUTHORIZED, "UNAUTHORIZED"));
        }

        List<DatabaseRecord> databaseRecordList = databaseRepository.findAllMainDatabasesOrderByName();

        List<DatabaseGraph> databaseGraphList = databaseRecordList.stream()
                .map(DatabaseGraph::new)
                .collect(Collectors.toList());

        return new DatabaseListPayload(databaseGraphList);
    }

}
