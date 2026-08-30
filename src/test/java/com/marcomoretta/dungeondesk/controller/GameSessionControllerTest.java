package com.marcomoretta.dungeondesk.controller;

import com.marcomoretta.dungeondesk.auth.AuthSession;
import com.marcomoretta.dungeondesk.auth.LoginType;
import com.marcomoretta.dungeondesk.domain.dto.GameSessionDto;
import com.marcomoretta.dungeondesk.domain.dto.request.CreateGameSessionRequestDto;
import com.marcomoretta.dungeondesk.domain.entity.AppUser;
import com.marcomoretta.dungeondesk.domain.entity.Campaign;
import com.marcomoretta.dungeondesk.domain.entity.GameSession;
import com.marcomoretta.dungeondesk.domain.request.CreateGameSessionRequest;
import com.marcomoretta.dungeondesk.exception.GameSessionNotFoundException;
import com.marcomoretta.dungeondesk.exception.ResourcePermissionException;
import com.marcomoretta.dungeondesk.mapper.GameSessionMapper;
import com.marcomoretta.dungeondesk.service.GameSessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameSessionControllerTest {

    private final static Long OWNER_ID = 1L;
    private final static Long CAMPAIGN_ID = 2L;
    private final static Long SESSION_ID = 3L;

    @Mock
    private GameSessionService gameSessionService;
    @Mock
    private GameSessionMapper gameSessionMapper;

    @InjectMocks
    private GameSessionController controller;

    private GameSession gameSession;
    private GameSessionDto gameSessionDto;

    private AuthSession masterSession;
    private AuthSession playerSession;

    @BeforeEach
    void setup() {
        // Arrange
        AppUser owner = AppUser.builder().userId(OWNER_ID).username("DM").build();
        Campaign campaign = Campaign.builder().campaignId(CAMPAIGN_ID).name("Campaign1")
                .owner(owner).build();

        gameSession = GameSession.builder().sessionId(SESSION_ID).campaign(campaign)
                .joinCode("ABC123").build();

        gameSessionDto = new GameSessionDto(SESSION_ID, CAMPAIGN_ID, "Campaign1",
                "ABC123", Instant.now(), null);

        masterSession = AuthSession.builder()
                .loginType(LoginType.MASTER).userId(OWNER_ID).displayName("DM").build();

        playerSession = AuthSession.builder()
                .loginType(LoginType.PLAYER).playerId(4L)
                .campaignId(CAMPAIGN_ID).displayName("Omega").build();
    }

    @Test
    void createSession() {
        // Arrange
        CreateGameSessionRequestDto dto = new CreateGameSessionRequestDto("ABC123");
        CreateGameSessionRequest request = new CreateGameSessionRequest(CAMPAIGN_ID, "ABC123");

        when(gameSessionMapper.fromCreateDto(dto, CAMPAIGN_ID)).thenReturn(request);
        when(gameSessionService.createNewSession(request, OWNER_ID)).thenReturn(gameSession);
        when(gameSessionMapper.toDto(gameSession)).thenReturn(gameSessionDto);

        // Act - Assert
        assertEquals(HttpStatus.CREATED,
                controller.createSession(CAMPAIGN_ID, dto, masterSession).getStatusCode());
    }

    @Test
    void getActiveSession_onlyMasterAllowed() {
        // Act - Assert
        assertThrows(ResourcePermissionException.class,
                () -> controller.getActiveSession(playerSession));

        verifyNoInteractions(gameSessionService);
    }

    @Test
    void getActiveSession_noOpenSession() {
        // Arrange
        when(gameSessionService.getActiveSession()).thenReturn(Optional.empty());

        // Act - Assert
        assertThrows(GameSessionNotFoundException.class,
                () -> controller.getActiveSession(masterSession));
    }

    @Test
    void closeSession() {
        // Arrange
        when(gameSessionService.closeSession(SESSION_ID, OWNER_ID)).thenReturn(gameSession);
        when(gameSessionMapper.toDto(gameSession)).thenReturn(gameSessionDto);

        // Act - Assert
        assertEquals(HttpStatus.OK,
                controller.closeSession(SESSION_ID, masterSession).getStatusCode());
    }
}