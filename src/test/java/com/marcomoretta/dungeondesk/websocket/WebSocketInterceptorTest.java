package com.marcomoretta.dungeondesk.websocket;

import com.marcomoretta.dungeondesk.auth.AuthInterceptor;
import com.marcomoretta.dungeondesk.auth.AuthSession;
import com.marcomoretta.dungeondesk.auth.LoginType;
import com.marcomoretta.dungeondesk.auth.SessionStore;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebSocketInterceptorTest {

    @Mock
    private SessionStore sessionStore;
    @Mock
    private ServletServerHttpRequest serverRequest;
    @Mock
    private HttpServletRequest servletRequest;
    @Mock
    private ServerHttpResponse serverResponse;
    @Mock
    private WebSocketHandler handler;

    @InjectMocks
    private WebSocketInterceptor webSocketInterceptor;

    private AuthSession session;

    @BeforeEach
    void setup() {
        // Arrange
        session = AuthSession.builder()
                .token("double-chocolate-cookie").loginType(LoginType.MASTER).userId(1L).displayName("DM").build();
    }

    @Test
    void beforeHandshake() throws Exception {
        // Arrange
        Map<String, Object> attributes = new HashMap<>();

        when(serverRequest.getServletRequest()).thenReturn(servletRequest);
        when(servletRequest.getCookies())
                .thenReturn(new Cookie[]{new Cookie(AuthInterceptor.COOKIE_NAME, "double-chocolate-cookie")});
        when(sessionStore.find("double-chocolate-cookie")).thenReturn(Optional.of(session));

        // Act
        boolean allowed = webSocketInterceptor.beforeHandshake(
                serverRequest, serverResponse, handler, attributes);

        // Assert
        assertTrue(allowed);
        assertEquals(session, attributes.get(AuthInterceptor.SESSION_ATTRIBUTE));
    }

    @Test
    void beforeHandshake_withoutACookie() throws Exception {
        // Arrange
        when(serverRequest.getServletRequest()).thenReturn(servletRequest);
        when(servletRequest.getCookies()).thenReturn(null);

        // Act
        boolean allowed = webSocketInterceptor.beforeHandshake(
                serverRequest, serverResponse, handler, new HashMap<>());

        // Assert
        assertFalse(allowed);
        verify(serverResponse).setStatusCode(HttpStatus.UNAUTHORIZED); // Handshake refused - 401
    }
}