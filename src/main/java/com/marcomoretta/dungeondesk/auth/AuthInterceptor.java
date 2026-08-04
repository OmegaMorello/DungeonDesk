package com.marcomoretta.dungeondesk.auth;

import com.marcomoretta.dungeondesk.exception.UnauthenticatedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor that can act between auth requests
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    public static final String SESSION_ATTRIBUTE = "authSession";
    private static final String BEARER_PREFIX = "Bearer ";
    private final SessionStore sessionStore;

    public AuthInterceptor(SessionStore sessionStore) {
        this.sessionStore = sessionStore;
    }

    /**
     * Acts as a middleware before the request is executed in a handler
     *
     * @return True if the session is found and the request can continue to the handler
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith(BEARER_PREFIX))
            throw new UnauthenticatedException();

        String token = header.substring(BEARER_PREFIX.length());

        AuthSession session = sessionStore.find(token).orElseThrow(UnauthenticatedException::new);
        request.setAttribute(SESSION_ATTRIBUTE, session);

        return true;
    }
}
