package com.marcomoretta.dungeondesk.auth;

import com.marcomoretta.dungeondesk.exception.UnauthenticatedException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthInterceptorTest {

    @Mock
    private SessionStore sessionStore;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthInterceptor authInterceptor;

    private AuthSession session;

    @BeforeEach
    void setup() {
        // Arrange
        session = AuthSession.builder()
                .token("triple-chocolate-cookie").loginType(LoginType.MASTER).userId(1L).displayName("DM").build();
    }

    @Test
    void preHandle() {
        // Arrange
        when(request.getCookies())
                .thenReturn(new Cookie[]{new Cookie(AuthInterceptor.COOKIE_NAME, "triple-chocolate-cookie")});
        when(sessionStore.find("triple-chocolate-cookie")).thenReturn(Optional.of(session));

        // Act
        boolean allowed = authInterceptor.preHandle(request, response, new Object());

        // Assert
        assertTrue(allowed);
        verify(request).setAttribute(AuthInterceptor.SESSION_ATTRIBUTE, session);
    }

    @Test
    void preHandle_withNoCookie() {
        // Arrange
        when(request.getCookies()).thenReturn(null);

        // Act - Assert
        assertThrows(UnauthenticatedException.class,
                () -> authInterceptor.preHandle(request, response, new Object()));

        verifyNoInteractions(sessionStore);
    }

    @Test
    void preHandle_withExpiredToken() {
        // Arrange
        when(request.getCookies())
                .thenReturn(new Cookie[]{new Cookie(AuthInterceptor.COOKIE_NAME, "stale")});
        when(sessionStore.find("stale")).thenReturn(Optional.empty());

        // Act - Assert
        assertThrows(UnauthenticatedException.class,
                () -> authInterceptor.preHandle(request, response, new Object()));

        verify(request, never()).setAttribute(any(), any());
    }
}