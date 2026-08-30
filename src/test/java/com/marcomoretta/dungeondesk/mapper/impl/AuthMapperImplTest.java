package com.marcomoretta.dungeondesk.mapper.impl;

import com.marcomoretta.dungeondesk.auth.AuthSession;
import com.marcomoretta.dungeondesk.auth.LoginType;
import com.marcomoretta.dungeondesk.domain.dto.AuthSessionDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthMapperImplTest {

    private final AuthMapperImpl authMapper = new AuthMapperImpl();

    @Test
    void toDto() {
        // Arrange
        AuthSession masterSession = AuthSession.builder()
                .loginType(LoginType.MASTER)
                .userId(1L)
                .displayName("Master")
                .build();

        AuthSession playerSession = AuthSession.builder()
                .loginType(LoginType.PLAYER)
                .playerId(2L)
                .campaignId(3L)
                .sessionId(4L)
                .displayName("Player")
                .build();

        // Act
        AuthSessionDto masterSessionDto = authMapper.toDto(masterSession);
        AuthSessionDto playerSessionDto = authMapper.toDto(playerSession);

        // Assert
        assertEquals("Master", masterSessionDto.displayName());
        assertNull(masterSessionDto.campaignId());
        assertEquals(2L, playerSessionDto.playerId());
        assertEquals(LoginType.PLAYER, playerSessionDto.loginType());
    }
}