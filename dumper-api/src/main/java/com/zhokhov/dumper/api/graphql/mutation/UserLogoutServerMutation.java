package com.zhokhov.dumper.api.graphql.mutation;

import com.zhokhov.dumper.api.graphql.error.SecurityError;
import com.zhokhov.dumper.api.graphql.error.code.SecurityErrorCode;
import com.zhokhov.dumper.api.graphql.payload.UserLogoutPayload;
import com.zhokhov.dumper.api.security.PolusharieSecurityService;
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

    private final PolusharieSecurityService polusharieSecurityService;
    private final ApplicationEventPublisher eventPublisher;
    private final AccessTokenCookieConfiguration accessTokenCookieConfiguration;
    private final RefreshTokenCookieConfiguration refreshTokenCookieConfiguration;

    public UserLogoutServerMutation(
            PolusharieSecurityService polusharieSecurityService,
            ApplicationEventPublisher eventPublisher,
            AccessTokenCookieConfiguration accessTokenCookieConfiguration,
            RefreshTokenCookieConfiguration refreshTokenCookieConfiguration
    ) {
        this.polusharieSecurityService = polusharieSecurityService;
        this.eventPublisher = eventPublisher;
        this.accessTokenCookieConfiguration = accessTokenCookieConfiguration;
        this.refreshTokenCookieConfiguration = refreshTokenCookieConfiguration;
    }

    public UserLogoutPayload userLogout(DataFetchingEnvironment env) {
        LOG.debug("Logout");

        HttpRequest request = getRequest(env.getContext());
        MutableHttpResponse<String> response = getResponse(env.getContext());

        if (!polusharieSecurityService.isLoggedIn()) {
            // TODO
            return new UserLogoutPayload(new SecurityError(SecurityErrorCode.UNAUTHORIZED, "UNAUTHORIZED"));
        }

        LOG.debug("Logging out");

        Authentication authentication = polusharieSecurityService.getAuthentication().orElseThrow();

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
