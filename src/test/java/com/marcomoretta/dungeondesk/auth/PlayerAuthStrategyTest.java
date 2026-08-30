package com.marcomoretta.dungeondesk.auth;

import com.marcomoretta.dungeondesk.domain.entity.AppUser;
import com.marcomoretta.dungeondesk.domain.entity.Campaign;
import com.marcomoretta.dungeondesk.domain.entity.GameSession;
import com.marcomoretta.dungeondesk.domain.entity.Player;
import com.marcomoretta.dungeondesk.exception.InvalidCredentialsException;
import com.marcomoretta.dungeondesk.repository.GameSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerAuthStrategyTest {

    @Mock
    private SessionStore sessionStore;
    @Mock
    private GameSessionRepository gameSessionRepository;

    @InjectMocks
    private PlayerAuthStrategy playerAuthStrategy;

    private Campaign campaign;
    private GameSession gameSession;

    @BeforeEach
    void setup() {
        // Arrange
        AppUser owner = AppUser.builder().userId(1L).username("DM").build();

        campaign = Campaign.builder().campaignId(2L).name("Campaign1").owner(owner).build();
        campaign.addPlayer(Player.builder().playerId(3L).name("Omega")
                .normalizedName("omega").campaign(campaign).build());

        gameSession = GameSession.builder().sessionId(5L).campaign(campaign)
                .joinCode("456123").build();
    }

    @Test
    void authenticate() {
        // Arrange
        when(gameSessionRepository.findByJoinCodeAndEndDateIsNull("456123"))
                .thenReturn(Optional.of(gameSession));

        // Act
        playerAuthStrategy.authenticate("Omega", "456123");

        // Assert - the session carries campaign and session, the player has no account
        verify(sessionStore).createPlayerSession(3L, 2L, 5L, "Omega");
    }

    @Test
    void authenticate_caseInsensitive() {
        // Arrange
        when(gameSessionRepository.findByJoinCodeAndEndDateIsNull("456123"))
                .thenReturn(Optional.of(gameSession));

        // Act
        playerAuthStrategy.authenticate("  OMEGA  ", "456123");

        // Assert - the roster is matched on the normalized name
        verify(sessionStore).createPlayerSession(3L, 2L, 5L, "Omega");
    }

    @Test
    void authenticate_wrongJoinCode() {
        // Arrange
        when(gameSessionRepository.findByJoinCodeAndEndDateIsNull("123456"))
                .thenReturn(Optional.empty());

        // Act - Assert
        assertThrows(InvalidCredentialsException.class,
                () -> playerAuthStrategy.authenticate("Omega", "123456"));
    }

    @Test
    void authenticate_wrongName() {
        // Arrange
        when(gameSessionRepository.findByJoinCodeAndEndDateIsNull("456123"))
                .thenReturn(Optional.of(gameSession));

        // Act - Assert
        assertThrows(InvalidCredentialsException.class,
                () -> playerAuthStrategy.authenticate("Daisy", "456123"));

        verifyNoInteractions(sessionStore);
    }

    @Test
    void supports() {
        // Act - Assert
        assertEquals(LoginType.PLAYER, playerAuthStrategy.supports());
    }
}