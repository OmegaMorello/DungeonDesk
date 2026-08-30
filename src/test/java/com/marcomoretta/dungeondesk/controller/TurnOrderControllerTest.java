package com.marcomoretta.dungeondesk.controller;

import com.marcomoretta.dungeondesk.auth.AuthSession;
import com.marcomoretta.dungeondesk.auth.LoginType;
import com.marcomoretta.dungeondesk.event.GameEventStream;
import com.marcomoretta.dungeondesk.event.TurnOrderChangedEvent;
import com.marcomoretta.dungeondesk.exception.ResourcePermissionException;

import com.marcomoretta.dungeondesk.service.TurnOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TurnOrderControllerTest {

    @Mock
    private TurnOrderService turnOrderService;
    @Mock
    private GameEventStream gameEventStream;

    @InjectMocks
    private TurnOrderController controller;

    private AuthSession masterSession;
    private AuthSession playerSession;

    @BeforeEach
    void setup() {
        // Arrange
        masterSession = AuthSession.builder()
                .loginType(LoginType.MASTER).userId(1L).displayName("DM").build();

        playerSession = AuthSession.builder()
                .loginType(LoginType.PLAYER).playerId(3L).displayName("Omega").build();
    }

    @Test
    void rollInitiative() {
        // Arrange
        when(turnOrderService.rollInitiative(masterSession)).thenReturn(List.of(2L, 1L));

        // Act
        ResponseEntity<List<Long>> response = controller.rollInitiative(masterSession);

        // Assert
        assertEquals(List.of(2L, 1L), response.getBody());
        verify(gameEventStream).notifyObservers(any(TurnOrderChangedEvent.class));
    }

    @Test
    void rollInitiative_onlyMasterAllowed() {
        // Act - Assert
        assertThrows(ResourcePermissionException.class,
                () -> controller.rollInitiative(playerSession));

        verifyNoInteractions(turnOrderService, gameEventStream);
    }

    @Test
    void setTurnOrder() {
        // Arrange
        List<Long> ids = List.of(3L, 1L, 2L);
        when(turnOrderService.setOrder(ids, masterSession)).thenReturn(ids);

        // Act - Assert
        assertEquals(ids, controller.setTurnOrder(ids, masterSession).getBody());
        verify(gameEventStream).notifyObservers(any(TurnOrderChangedEvent.class));
    }
}