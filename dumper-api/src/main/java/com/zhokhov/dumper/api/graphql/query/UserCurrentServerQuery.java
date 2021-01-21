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
import com.zhokhov.dumper.api.graphql.payload.UserCurrentPayload;
import com.zhokhov.dumper.api.security.DumperSecurityService;
import com.zhokhov.dumper.data.jooq.tables.records.AccountRecord;
import graphql.kickstart.tools.GraphQLQueryResolver;

import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class UserCurrentServerQuery implements GraphQLQueryResolver {

    private final DumperSecurityService dumperSecurityService;

    public UserCurrentServerQuery(DumperSecurityService dumperSecurityService) {
        this.dumperSecurityService = dumperSecurityService;
    }

    public UserCurrentPayload userCurrent() {
        Optional<AccountRecord> account = dumperSecurityService.getCurrentUser();

        if (account.isPresent()) {
            return new UserCurrentPayload(new UserGraph(account.get()));
        }

        // TODO
        return new UserCurrentPayload(new SecurityError(SecurityErrorCode.UNAUTHORIZED, ""));
    }

}
