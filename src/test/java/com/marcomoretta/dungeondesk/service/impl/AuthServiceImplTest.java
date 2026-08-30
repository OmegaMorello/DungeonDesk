package com.marcomoretta.dungeondesk.service.impl;

import com.marcomoretta.dungeondesk.auth.AuthSession;
import com.marcomoretta.dungeondesk.auth.AuthStrategy;
import com.marcomoretta.dungeondesk.auth.LoginType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthStrategy masterStrategy;
    @Mock
    private AuthStrategy playerStrategy;

    private AuthServiceImpl authService;

    @BeforeEach
    void setup() {
        // Arrange
        when(masterStrategy.supports()).thenReturn(LoginType.MASTER);
        when(playerStrategy.supports()).thenReturn(LoginType.PLAYER);

        authService = new AuthServiceImpl(List.of(masterStrategy, playerStrategy));
    }

    @Test
    void login() {
        // Arrange
        AuthSession expected = AuthSession.builder()
                .loginType(LoginType.MASTER).userId(1L).displayName("DM").build();

        when(masterStrategy.authenticate("DM", "secret")).thenReturn(expected);

        // Act - Assert
        assertEquals(expected, authService.login(LoginType.MASTER, "DM", "secret"));
        verify(playerStrategy, never()).authenticate(any(), any());
    }

}