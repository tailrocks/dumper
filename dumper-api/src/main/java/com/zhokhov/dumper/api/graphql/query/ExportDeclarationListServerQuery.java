/*
 * Copyright 2021 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
