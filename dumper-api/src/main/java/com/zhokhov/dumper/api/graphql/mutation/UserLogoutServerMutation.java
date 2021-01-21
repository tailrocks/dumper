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

import com.zhokhov.dumper.api.graphql.error.SecurityError;
import com.zhokhov.dumper.api.graphql.error.code.SecurityErrorCode;
import com.zhokhov.dumper.api.graphql.payload.UserLogoutPayload;
import com.zhokhov.dumper.api.security.DumperSecurityService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.schema.DataFetchingEnvironment;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.http.cookie.CookieConfiguration;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.event.LogoutEvent;
import io.micronaut.security.token.jwt.cookie.AccessTokenCookieConfiguration;
import io.micronaut.security.token.jwt.cookie.RefreshTokenCookieConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.Optional;

import static com.zhokhov.jambalaya.micronaut.graphql.GraphQLContextHttpUtils.getRequest;
import static com.zhokhov.jambalaya.micronaut.graphql.GraphQLContextHttpUtils.getResponse;

@Singleton
public class UserLogoutServerMutation implements GraphQLMutationResolver {

    private static final Logger LOG = LoggerFactory.getLogger(UserLogoutServerMutation.class);

    private final DumperSecurityService dumperSecurityService;
    private final ApplicationEventPublisher eventPublisher;
    private final AccessTokenCookieConfiguration accessTokenCookieConfiguration;
    private final RefreshTokenCookieConfiguration refreshTokenCookieConfiguration;

    public UserLogoutServerMutation(
            DumperSecurityService dumperSecurityService,
            ApplicationEventPublisher eventPublisher,
            AccessTokenCookieConfiguration accessTokenCookieConfiguration,
            RefreshTokenCookieConfiguration refreshTokenCookieConfiguration
    ) {
        this.dumperSecurityService = dumperSecurityService;
        this.eventPublisher = eventPublisher;
        this.accessTokenCookieConfiguration = accessTokenCookieConfiguration;
        this.refreshTokenCookieConfiguration = refreshTokenCookieConfiguration;
    }

    public UserLogoutPayload userLogout(DataFetchingEnvironment env) {
        LOG.debug("Logout");

        HttpRequest request = getRequest(env.getContext());
        MutableHttpResponse<String> response = getResponse(env.getContext());

        if (!dumperSecurityService.isLoggedIn()) {
            // TODO
            return new UserLogoutPayload(new SecurityError(SecurityErrorCode.UNAUTHORIZED, "UNAUTHORIZED"));
        }

        LOG.debug("Logging out");

        Authentication authentication = dumperSecurityService.getAuthentication().orElseThrow();

        eventPublisher.publishEvent(new LogoutEvent(authentication));

        clearCookie(accessTokenCookieConfiguration, request, response);
        if (refreshTokenCookieConfiguration != null) {
            clearCookie(refreshTokenCookieConfiguration, request, response);
        }

        return new UserLogoutPayload();
    }

    private void clearCookie(CookieConfiguration cookieConfiguration, HttpRequest<?> request, MutableHttpResponse<?> response) {
        Optional<Cookie> cookie = request.getCookies().findCookie(cookieConfiguration.getCookieName());
        if (cookie.isPresent()) {
            Cookie requestCookie = cookie.get();
            String domain = cookieConfiguration.getCookieDomain().orElse(null);
            String path = cookieConfiguration.getCookiePath().orElse(null);
            Cookie responseCookie = Cookie.of(requestCookie.getName(), "");
            responseCookie.maxAge(0).domain(domain).path(path);
            response.cookie(responseCookie);
        }
    }

}
