package com.zhokhov.dumper.api.graphql.query;

import com.zhokhov.dumper.api.graphql.error.SecurityError;
import com.zhokhov.dumper.api.graphql.error.code.SecurityErrorCode;
import com.zhokhov.dumper.api.graphql.model.ExportDeclarationGraph;
import com.zhokhov.dumper.api.graphql.payload.ExportDeclarationListPayload;
import com.zhokhov.dumper.api.security.DumperSecurityService;
import com.zhokhov.dumper.data.repository.ExportDeclarationRepository;
import graphql.kickstart.tools.GraphQLQueryResolver;

import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class ExportDeclarationListServerQuery implements GraphQLQueryResolver {

    private final ExportDeclarationRepository exportDeclarationRepository;
    private final DumperSecurityService dumperSecurityService;

    public ExportDeclarationListServerQuery(ExportDeclarationRepository exportDeclarationRepository,
                                            DumperSecurityService dumperSecurityService) {
        this.exportDeclarationRepository = exportDeclarationRepository;
        this.dumperSecurityService = dumperSecurityService;
    }

    public ExportDeclarationListPayload exportDeclarationList() {
        if (!dumperSecurityService.isLoggedIn()) {
            // TODO
            return new ExportDeclarationListPayload(new SecurityError(SecurityErrorCode.UNAUTHORIZED, "UNAUTHORIZED"));
        }

        List<ExportDeclarationGraph> data = exportDeclarationRepository
                .findAll()
                .stream()
                .map(ExportDeclarationGraph::new)
                .collect(Collectors.toList());

        return new ExportDeclarationListPayload(data);
    }

}
