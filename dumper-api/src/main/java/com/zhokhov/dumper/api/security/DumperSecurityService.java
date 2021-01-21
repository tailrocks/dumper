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
