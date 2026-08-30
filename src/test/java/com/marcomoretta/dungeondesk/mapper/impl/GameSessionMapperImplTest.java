package com.marcomoretta.dungeondesk.mapper.impl;

import com.marcomoretta.dungeondesk.domain.dto.GameSessionDto;
import com.marcomoretta.dungeondesk.domain.dto.SessionPlayerDto;
import com.marcomoretta.dungeondesk.domain.dto.request.CreateGameSessionRequestDto;
import com.marcomoretta.dungeondesk.domain.dto.request.UpdateGameSessionRequestDto;
import com.marcomoretta.dungeondesk.domain.entity.AppUser;
import com.marcomoretta.dungeondesk.domain.entity.Campaign;
import com.marcomoretta.dungeondesk.domain.entity.GameSession;
import com.marcomoretta.dungeondesk.domain.entity.Player;
import com.marcomoretta.dungeondesk.domain.request.CreateGameSessionRequest;
import com.marcomoretta.dungeondesk.domain.request.UpdateGameSessionRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameSessionMapperImplTest {

    private final GameSessionMapperImpl gameSessionMapper = new GameSessionMapperImpl();

    // Arrange
    private final GameSession gameSession = GameSession.builder()
            .campaign(Campaign.builder()
                    .name("TestCampaign")
                    .description("CampaignDesc")
                    .owner(AppUser.builder().build())
                    .players(List.of(Player.builder().name("P1").build(),
                            Player.builder().name("P2").build()))
                    .build())
            .joinCode("123123")
            .build();

    @Test
    void toDto() {
        // Act
        GameSessionDto gameSessionDto = gameSessionMapper.toDto(gameSession);

        // Assert
        assertEquals("TestCampaign", gameSessionDto.campaignName());
        assertEquals("123123", gameSessionDto.joinCode());
    }

    @Test
    void toSessionPlayerDtoList() {
        // Act
        List<SessionPlayerDto> sessionPlayerDtoList = gameSessionMapper.toSessionPlayerDtoList(gameSession.getCampaign().getPlayers());

        // Assert
        assertEquals(2, sessionPlayerDtoList.size());
        assertEquals("P2", sessionPlayerDtoList.getLast().name());
    }

    @Test
    void fromCreateDto() {
        // Arrange
        CreateGameSessionRequestDto createGameSessionRequestDto = new CreateGameSessionRequestDto("123456");

        // Act
        CreateGameSessionRequest createGameSessionRequest = gameSessionMapper.fromCreateDto(createGameSessionRequestDto, 1L);

        // Assert
        assertEquals("123456", createGameSessionRequest.joinCode());
        assertEquals(1, createGameSessionRequest.campaignId());
    }

    @Test
    void fromUpdateDto() {
        // Arrange
        UpdateGameSessionRequestDto updateGameSessionRequestDto = new UpdateGameSessionRequestDto("654321");

        // Act
        UpdateGameSessionRequest updateGameSessionRequest = gameSessionMapper.fromUpdateDto(updateGameSessionRequestDto, 2L);

        // Assert
        assertEquals("654321", updateGameSessionRequest.joinCode());
        assertEquals(2, updateGameSessionRequest.sessionId());
    }

}