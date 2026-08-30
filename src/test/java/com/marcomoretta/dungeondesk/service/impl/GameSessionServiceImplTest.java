package com.marcomoretta.dungeondesk.service.impl;

import com.marcomoretta.dungeondesk.domain.entity.AppUser;
import com.marcomoretta.dungeondesk.domain.entity.Campaign;
import com.marcomoretta.dungeondesk.domain.entity.GameSession;
import com.marcomoretta.dungeondesk.domain.entity.Player;
import com.marcomoretta.dungeondesk.domain.request.CreateGameSessionRequest;
import com.marcomoretta.dungeondesk.domain.request.UpdateGameSessionRequest;
import com.marcomoretta.dungeondesk.exception.GameSessionNotFoundException;
import com.marcomoretta.dungeondesk.exception.GameSessionPermissionException;
import com.marcomoretta.dungeondesk.exception.SessionAlreadyOpenException;
import com.marcomoretta.dungeondesk.repository.GameSessionRepository;
import com.marcomoretta.dungeondesk.service.CampaignService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameSessionServiceImplTest {

    // Using fixed ids for ease of use
    private final static Long OWNER_ID = 1L;
    private final static Long CAMPAIGN_ID = 2L;
    private final static Long SESSION_ID = 3L;
    private final static Long OTHER_ID = 99L;

    @Mock
    private GameSessionRepository gameSessionRepository;
    @Mock
    private CampaignService campaignService;

    @InjectMocks
    private GameSessionServiceImpl gameSessionService;

    private Campaign campaign;
    private GameSession gameSession;

    @BeforeEach
    void setup() {
        // Arrange
        AppUser owner = AppUser.builder().userId(OWNER_ID).username("DM").build();

        campaign = Campaign.builder().campaignId(CAMPAIGN_ID).name("Campaign1").owner(owner).build();
        gameSession = GameSession.builder().sessionId(SESSION_ID).campaign(campaign)
                .joinCode("ABC123").build();
    }

    @Test
    void getActiveSessionRoster() {
        // Arrange
        campaign.addPlayer(Player.builder().playerId(4L).name("Omega").campaign(campaign).build());
        when(gameSessionRepository.findByEndDateIsNull()).thenReturn(Optional.of(gameSession));

        // Act - Assert
        assertEquals(1, gameSessionService.getActiveSessionRoster().size());
    }

    @Test
    void getActiveSessionRoster_empty() {
        // Arrange - no open session -> the login screen shows a message
        when(gameSessionRepository.findByEndDateIsNull()).thenReturn(Optional.empty());

        // Act - Assert
        assertTrue(gameSessionService.getActiveSessionRoster().isEmpty());
    }

    @Test
    void createNewSession() {
        // Arrange
        when(campaignService.getCampaign(CAMPAIGN_ID)).thenReturn(campaign);
        when(gameSessionRepository.findByEndDateIsNull()).thenReturn(Optional.empty());
        when(gameSessionRepository.save(any())).thenAnswer(call -> call.getArgument(0));

        // Act
        GameSession created = gameSessionService.createNewSession(
                new CreateGameSessionRequest(CAMPAIGN_ID, "a1s2d3"), OWNER_ID);

        // Assert
        assertEquals("a1s2d3", created.getJoinCode());
        assertEquals(campaign, created.getCampaign());
        assertNull(created.getEndDate());
    }

    @Test
    void createNewSession_onlyOneSessionOpen() {
        // Arrange
        when(campaignService.getCampaign(CAMPAIGN_ID)).thenReturn(campaign);
        when(gameSessionRepository.findByEndDateIsNull()).thenReturn(Optional.of(gameSession));

        // Act - Assert
        assertThrows(SessionAlreadyOpenException.class, () -> gameSessionService.createNewSession(
                new CreateGameSessionRequest(CAMPAIGN_ID, "a1s2d3"), OWNER_ID));

        verify(gameSessionRepository, never()).save(any());
    }

    @Test
    void createNewSession_onlyOwnerAllowed() {
        // Arrange
        when(campaignService.getCampaign(CAMPAIGN_ID)).thenReturn(campaign);

        // Act - Assert
        assertThrows(GameSessionPermissionException.class, () -> gameSessionService.createNewSession(
                new CreateGameSessionRequest(CAMPAIGN_ID, "a1s2d3"), OTHER_ID));
    }

    @Test
    void closeSession() {
        // Arrange
        when(gameSessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(gameSession));
        when(gameSessionRepository.save(any())).thenAnswer(call -> call.getArgument(0));

        // Act
        GameSession closed = gameSessionService.closeSession(SESSION_ID, OWNER_ID);

        // Assert
        assertNotNull(closed.getEndDate());
    }

    @Test
    void updateSession() {
        // Arrange
        when(gameSessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(gameSession));
        when(gameSessionRepository.save(any())).thenAnswer(call -> call.getArgument(0));

        // Act
        GameSession updated = gameSessionService.updateSession(
                new UpdateGameSessionRequest(SESSION_ID, "qwerty"), OWNER_ID);

        // Assert
        assertEquals("qwerty", updated.getJoinCode());
    }

    @Test
    void getSession_missingSession() {
        // Arrange
        when(gameSessionRepository.findById(SESSION_ID)).thenReturn(Optional.empty());

        // Act - Assert
        assertThrows(GameSessionNotFoundException.class,
                () -> gameSessionService.closeSession(SESSION_ID, OWNER_ID));
    }
}