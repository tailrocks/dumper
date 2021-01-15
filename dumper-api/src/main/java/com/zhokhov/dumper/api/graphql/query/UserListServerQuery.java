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
