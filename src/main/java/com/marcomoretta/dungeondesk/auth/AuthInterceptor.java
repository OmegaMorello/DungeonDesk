package com.marcomoretta.dungeondesk.auth;

import com.marcomoretta.dungeondesk.exception.UnauthenticatedException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.WebUtils;

/**
 * Interceptor that can act between auth requests
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    public static final String SESSION_ATTRIBUTE = "auth_session";
    public static final String COOKIE_NAME = "dd_session";
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
        Cookie cookie = WebUtils.getCookie(request, COOKIE_NAME);
        if (cookie == null) throw new UnauthenticatedException();

        String token = cookie.getValue();

        AuthSession session = sessionStore.find(token).orElseThrow(UnauthenticatedException::new);
        request.setAttribute(SESSION_ATTRIBUTE, session);

        return true;
    }
}
