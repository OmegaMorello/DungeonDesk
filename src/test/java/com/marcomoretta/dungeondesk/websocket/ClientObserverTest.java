package com.marcomoretta.dungeondesk.websocket;

import com.marcomoretta.dungeondesk.auth.AuthSession;
import com.marcomoretta.dungeondesk.auth.LoginType;
import com.marcomoretta.dungeondesk.command.DiceType;
import com.marcomoretta.dungeondesk.event.ChatEvent;
import com.marcomoretta.dungeondesk.event.DiceRolledEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientObserverTest {

    @Mock
    private WebSocketSession webSocketSession;

    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    private AuthSession masterSession;
    private AuthSession playerSession;

    @BeforeEach
    void setup() {
        // Arrange
        masterSession = AuthSession.builder()
                .loginType(LoginType.MASTER).userId(1L).displayName("DM").build();

        playerSession = AuthSession.builder()
                .loginType(LoginType.PLAYER).playerId(3L)
                .campaignId(2L).displayName("Omega").build();
    }

    @Test
    void onEvent() throws Exception {
        // Arrange
        when(webSocketSession.isOpen()).thenReturn(true);
        ClientObserver observer = new ClientObserver(webSocketSession, playerSession, jsonMapper);

        // Act
        observer.onEvent(new ChatEvent("DM", null, "Hello fellows", Instant.now()));

        // Assert
        verify(webSocketSession).sendMessage(any(TextMessage.class));
    }

    @Test
    void onEvent_hiddenRoll() throws Exception {
        // Arrange
        ClientObserver observer = new ClientObserver(webSocketSession, playerSession, jsonMapper);

        // Act
        observer.onEvent(new DiceRolledEvent("DM", DiceType.D20, 20, true, Instant.now()));

        // Assert
        verify(webSocketSession, never()).sendMessage(any()); // Not sending a hidden dice roll to players
    }

    @Test
    void onEvent_deliverHiddenRollToMaster() throws Exception {
        // Arrange
        when(webSocketSession.isOpen()).thenReturn(true);
        ClientObserver observer = new ClientObserver(webSocketSession, masterSession, jsonMapper);

        // Act
        observer.onEvent(new DiceRolledEvent("DM", DiceType.D20, 20, true, Instant.now()));

        // Assert
        verify(webSocketSession).sendMessage(any(TextMessage.class));
    }

    @Test
    void onEvent_whisperToPlayer() throws Exception {
        // Arrange
        when(webSocketSession.isOpen()).thenReturn(true);
        ClientObserver observer = new ClientObserver(webSocketSession, playerSession, jsonMapper);

        // Act
        observer.onEvent(new ChatEvent("DM", "Omega", "Only for you", Instant.now()));

        // Assert
        verify(webSocketSession).sendMessage(any(TextMessage.class));
    }

    @Test
    void onEvent_masterCanSeeOwnWhispers() throws Exception {
        // Arrange
        when(webSocketSession.isOpen()).thenReturn(true);
        ClientObserver observer = new ClientObserver(webSocketSession, masterSession, jsonMapper);

        // Act
        observer.onEvent(new ChatEvent("DM", "Omega", "Only for you", Instant.now()));

        // Assert
        verify(webSocketSession).sendMessage(any(TextMessage.class));
    }

    @Test
    void onEvent_hideWhisperToOtherPlayers() throws Exception {
        // Arrange
        ClientObserver observer = new ClientObserver(webSocketSession, playerSession, jsonMapper);

        // Act - addressed to somebody else
        observer.onEvent(new ChatEvent("DM", "Toadette", "Only for you", Instant.now()));

        // Assert
        verify(webSocketSession, never()).sendMessage(any());
    }

    @Test
    void onEvent_closedSession() throws Exception {
        // Arrange
        when(webSocketSession.isOpen()).thenReturn(false);
        ClientObserver observer = new ClientObserver(webSocketSession, playerSession, jsonMapper);

        // Act
        observer.onEvent(new ChatEvent("DM", null, "Hello", Instant.now()));

        // Assert
        verify(webSocketSession, never()).sendMessage(any());
    }

    @Test
    void onEvent_survivesAFailedDelivery() throws Exception {
        // Arrange
        when(webSocketSession.isOpen()).thenReturn(true);
        doThrow(new IOException("mamma mia")).when(webSocketSession).sendMessage(any());

        ClientObserver observer = new ClientObserver(webSocketSession, playerSession, jsonMapper);

        // Act - Assert
        assertDoesNotThrow(() ->
                observer.onEvent(new ChatEvent("DM", null, "Hello", Instant.now())));
    }
}