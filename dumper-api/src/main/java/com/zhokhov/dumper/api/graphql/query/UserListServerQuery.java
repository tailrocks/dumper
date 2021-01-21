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
import com.zhokhov.dumper.api.graphql.model.UserGraph;
import com.zhokhov.dumper.api.graphql.payload.UserListPayload;
import com.zhokhov.dumper.api.security.DumperSecurityService;
import com.zhokhov.dumper.data.jooq.tables.records.AccountRecord;
import com.zhokhov.dumper.data.repository.AccountRepository;
import graphql.kickstart.tools.GraphQLQueryResolver;

import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class UserListServerQuery implements GraphQLQueryResolver {

    private final AccountRepository accountRepository;
    private final DumperSecurityService dumperSecurityService;

    public UserListServerQuery(AccountRepository accountRepository,
                               DumperSecurityService dumperSecurityService) {
        this.accountRepository = accountRepository;
        this.dumperSecurityService = dumperSecurityService;
    }

    public UserListPayload userList() {
        if (!dumperSecurityService.isLoggedIn()) {
            // TODO
            return new UserListPayload(new SecurityError(SecurityErrorCode.UNAUTHORIZED, "UNAUTHORIZED"));
        }

        List<AccountRecord> accountList = accountRepository.findAllOrderByUsername();

        List<UserGraph> userGraphList = accountList.stream()
                .map(UserGraph::new)
                .collect(Collectors.toList());

        return new UserListPayload(userGraphList);
    }

}
