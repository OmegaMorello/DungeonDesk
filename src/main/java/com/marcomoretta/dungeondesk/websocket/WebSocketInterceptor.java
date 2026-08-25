package com.marcomoretta.dungeondesk.websocket;

import com.marcomoretta.dungeondesk.auth.AuthInterceptor;
import com.marcomoretta.dungeondesk.auth.AuthSession;
import com.marcomoretta.dungeondesk.auth.SessionStore;
import com.marcomoretta.dungeondesk.exception.UnauthenticatedException;
import jakarta.servlet.http.Cookie;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.WebUtils;

import java.util.Map;

/**
 * Authenticates the web socket handshake
 */
@Component
public class WebSocketInterceptor implements HandshakeInterceptor {

    private final SessionStore sessionStore;

    public WebSocketInterceptor(SessionStore sessionStore) {
        this.sessionStore = sessionStore;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {

        if (!(request instanceof ServletServerHttpRequest servletServerHttpRequest)) return false;

        Cookie cookie = WebUtils.getCookie(servletServerHttpRequest.getServletRequest(), AuthInterceptor.COOKIE_NAME);
        if (cookie == null) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        String token = cookie.getValue();

        AuthSession session = sessionStore.find(token).orElseThrow(UnauthenticatedException::new);
        attributes.put(AuthInterceptor.SESSION_ATTRIBUTE, session);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, @Nullable Exception exception) {

    }
}
