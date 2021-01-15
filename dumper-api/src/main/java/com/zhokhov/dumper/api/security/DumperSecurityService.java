package com.zhokhov.dumper.api.security;

import com.zhokhov.dumper.data.jooq.tables.records.AccountRecord;
import com.zhokhov.dumper.data.repository.AccountRepository;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.utils.SecurityService;

import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class DumperSecurityService {

    private final AccountRepository accountRepository;
    private final SecurityService securityService;

    public DumperSecurityService(AccountRepository accountRepository, SecurityService securityService) {
        this.accountRepository = accountRepository;
        this.securityService = securityService;
    }

    public Optional<Authentication> getAuthentication() {
        return securityService.getAuthentication();
    }

    public Optional<AccountRecord> getCurrentUser() {
        return securityService.getAuthentication()
                .flatMap(it -> accountRepository.findByUsername(it.getName()));
    }

    public Optional<String> getUsername() {
        return securityService.username();
    }

    public boolean isLoggedIn() {
        return securityService.isAuthenticated();
    }

}
