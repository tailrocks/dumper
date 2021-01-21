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
package com.zhokhov.dumper.api.graphql.mutation;

import com.zhokhov.dumper.api.graphql.error.LoginError;
import com.zhokhov.dumper.api.graphql.error.code.LoginErrorCode;
import com.zhokhov.dumper.api.graphql.input.UserLoginInput;
import com.zhokhov.dumper.api.graphql.model.UserGraph;
import com.zhokhov.dumper.api.graphql.payload.UserLoginPayload;
import com.zhokhov.dumper.data.jooq.tables.records.AccountRecord;
import com.zhokhov.dumper.data.repository.AccountRepository;
import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.schema.DataFetchingEnvironment;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.security.authentication.AuthenticationFailed;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.Authenticator;
import io.micronaut.security.authentication.UserDetails;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.event.LoginSuccessfulEvent;
import io.micronaut.security.token.jwt.cookie.AccessTokenCookieConfiguration;
import io.micronaut.security.token.jwt.generator.AccessRefreshTokenGenerator;
import io.micronaut.security.token.jwt.generator.AccessTokenConfiguration;
import io.micronaut.security.token.jwt.render.AccessRefreshToken;
import io.reactivex.Flowable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.time.temporal.TemporalAmount;
import java.util.Optional;

import static com.zhokhov.jambalaya.micronaut.graphql.GraphQLContextHttpUtils.getRequest;
import static com.zhokhov.jambalaya.micronaut.graphql.GraphQLContextHttpUtils.getResponse;

@Singleton
public class UserLoginServerMutation implements GraphQLMutationResolver {

    private static final Logger LOG = LoggerFactory.getLogger(UserLoginServerMutation.class);

    private final AccountRepository accountRepository;
    private final Authenticator authenticator;
    private final ApplicationEventPublisher eventPublisher;
    private final AccessTokenCookieConfiguration jwtCookieConfiguration;
    private final AccessRefreshTokenGenerator accessRefreshTokenGenerator;
    private final AccessTokenConfiguration jwtGeneratorConfiguration;

    public UserLoginServerMutation(
            AccountRepository accountRepository,
            Authenticator authenticator,
            ApplicationEventPublisher eventPublisher,
            AccessTokenCookieConfiguration jwtCookieConfiguration,
            AccessRefreshTokenGenerator accessRefreshTokenGenerator,
            AccessTokenConfiguration jwtGeneratorConfiguration
    ) {
        this.accountRepository = accountRepository;
        this.authenticator = authenticator;
        this.eventPublisher = eventPublisher;
        this.jwtCookieConfiguration = jwtCookieConfiguration;
        this.accessRefreshTokenGenerator = accessRefreshTokenGenerator;
        this.jwtGeneratorConfiguration = jwtGeneratorConfiguration;
    }

    public UserLoginPayload userLogin(UserLoginInput input, DataFetchingEnvironment env) {
        String username = input.getUsername().toLowerCase().trim().strip();
        String password = input.getPassword().trim().strip();

        LOG.debug("Attempt to login with {} : {}", username, password);

        HttpRequest httpRequest = getRequest(env.getContext());
        MutableHttpResponse<String> httpResponse = getResponse(env.getContext());

        UsernamePasswordCredentials usernamePasswordCredentials = new UsernamePasswordCredentials(username, password);

        Flowable<AuthenticationResponse> authenticationResponseFlowable =
                Flowable.fromPublisher(authenticator.authenticate(httpRequest, usernamePasswordCredentials));

        return authenticationResponseFlowable.map(authenticationResponse -> {
            if (authenticationResponse.isAuthenticated()) {
                LOG.debug("Successfully authenticated {}", username);

                UserDetails userDetails = (UserDetails) authenticationResponse;
                eventPublisher.publishEvent(new LoginSuccessfulEvent(userDetails));

                Optional<Cookie> jwtCookie = accessTokenCookie(userDetails, httpRequest);
                jwtCookie.ifPresent(httpResponse::cookie);

                AccountRecord user = accountRepository.findByUsername(userDetails.getUsername()).orElseThrow();

                return new UserLoginPayload(new UserGraph(user));
            } else {
                AuthenticationFailed authenticationFailed = (AuthenticationFailed) authenticationResponse;

                LOG.debug("Failed authenticate {} - {} - {}", username, authenticationFailed.getReason(), authenticationFailed.getMessage().orElse(null));

                switch (authenticationFailed.getReason()) {
                    case USER_NOT_FOUND:
                        return new UserLoginPayload(new LoginError(LoginErrorCode.USER_NOT_FOUND, "error"));
                    case CREDENTIALS_DO_NOT_MATCH:
                        return new UserLoginPayload(new LoginError(LoginErrorCode.USER_NOT_FOUND, "error"));
                    default:
                        throw new IllegalStateException("Unexpected value: " + authenticationFailed.getReason());
                }
            }
        }).blockingFirst();
    }

    private Optional<Cookie> accessTokenCookie(UserDetails userDetails, HttpRequest<?> request) {
        Optional<AccessRefreshToken> accessRefreshTokenOptional = accessRefreshTokenGenerator.generate(userDetails);
        if (accessRefreshTokenOptional.isPresent()) {
            Cookie cookie = Cookie.of(jwtCookieConfiguration.getCookieName(), accessRefreshTokenOptional.get().getAccessToken());
            cookie.configure(jwtCookieConfiguration, request.isSecure());
            Optional<TemporalAmount> cookieMaxAge = jwtCookieConfiguration.getCookieMaxAge();
            if (cookieMaxAge.isPresent()) {
                cookie.maxAge(cookieMaxAge.get());
            } else {
                cookie.maxAge(jwtGeneratorConfiguration.getExpiration());
            }
            return Optional.of(cookie);
        }
        return Optional.empty();
    }
}
