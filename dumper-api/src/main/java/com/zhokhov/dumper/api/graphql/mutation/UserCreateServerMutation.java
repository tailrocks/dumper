package com.zhokhov.dumper.api.graphql.mutation;

import com.zhokhov.dumper.api.graphql.error.SecurityError;
import com.zhokhov.dumper.api.graphql.error.code.SecurityErrorCode;
import com.zhokhov.dumper.api.graphql.input.UserCreateInput;
import com.zhokhov.dumper.api.graphql.model.UserGraph;
import com.zhokhov.dumper.api.graphql.payload.UserCreatePayload;
import com.zhokhov.dumper.api.security.DumperSecurityService;
import com.zhokhov.dumper.api.security.PasswordEncoder;
import com.zhokhov.dumper.data.jooq.tables.records.AccountRecord;
import com.zhokhov.dumper.data.repository.AccountRepository;
import graphql.kickstart.tools.GraphQLMutationResolver;

import javax.inject.Singleton;

@Singleton
public class UserCreateServerMutation implements GraphQLMutationResolver {

    private final AccountRepository accountRepository;
    private final DumperSecurityService dumperSecurityService;
    private final PasswordEncoder passwordEncoder;

    public UserCreateServerMutation(AccountRepository accountRepository,
                                    DumperSecurityService dumperSecurityService,
                                    PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.dumperSecurityService = dumperSecurityService;
        this.passwordEncoder = passwordEncoder;
    }

    public UserCreatePayload userCreate(UserCreateInput input) {
        if (!dumperSecurityService.isLoggedIn()) {
            // TODO
            return new UserCreatePayload(new SecurityError(SecurityErrorCode.UNAUTHORIZED, "UNAUTHORIZED"));
        }

        String encodedPassword = passwordEncoder.encode(input.getPassword());

        AccountRecord account = accountRepository.create(
                input.getUsername(),
                encodedPassword,
                input.getEmail(),
                input.getFirstName(),
                input.getLastName()
        );

        return new UserCreatePayload(new UserGraph(account));
    }

}
