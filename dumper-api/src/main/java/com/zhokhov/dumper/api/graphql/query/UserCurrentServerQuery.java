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
