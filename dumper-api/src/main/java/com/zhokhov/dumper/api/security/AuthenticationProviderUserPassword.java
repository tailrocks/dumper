package com.zhokhov.dumper.api.security;

import com.zhokhov.dumper.data.jooq.tables.records.AccountRecord;
import com.zhokhov.dumper.data.repository.AccountRepository;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationFailed;
import io.micronaut.security.authentication.AuthenticationFailureReason;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.UserDetails;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;

import javax.inject.Singleton;
import java.util.Collections;
import java.util.Optional;

@Singleton
public class AuthenticationProviderUserPassword implements AuthenticationProvider {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationProviderUserPassword(
            AccountRepository accountRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Publisher<AuthenticationResponse> authenticate(@Nullable HttpRequest<?> httpRequest,
                                                          AuthenticationRequest<?, ?> authenticationRequest) {
        String username = (String) authenticationRequest.getIdentity();

        Optional<AccountRecord> user = accountRepository.findByUsername(username);

        if (user.isEmpty()) {
            return Flowable.just(new AuthenticationFailed(AuthenticationFailureReason.USER_NOT_FOUND));
        }

        String rawPassword = (String) authenticationRequest.getSecret();

        boolean passwordMatched = user.get().getPassword() != null &&
                passwordEncoder.matches(rawPassword, user.get().getPassword());

        if (passwordMatched) {
            return Flowable.just(new UserDetails(user.get().getUsername(), Collections.emptyList()));
        }

        return Flowable.just(new AuthenticationFailed(AuthenticationFailureReason.CREDENTIALS_DO_NOT_MATCH));
    }

}
