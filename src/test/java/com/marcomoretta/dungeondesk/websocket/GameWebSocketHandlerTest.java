package com.marcomoretta.dungeondesk.websocket;

import com.marcomoretta.dungeondesk.auth.AuthInterceptor;
import com.marcomoretta.dungeondesk.auth.AuthSession;
import com.marcomoretta.dungeondesk.auth.LoginType;
import com.marcomoretta.dungeondesk.command.ChatCommand;
import com.marcomoretta.dungeondesk.command.Command;
import com.marcomoretta.dungeondesk.command.CommandQueue;
import com.marcomoretta.dungeondesk.command.GameState;
import com.marcomoretta.dungeondesk.event.ChatEvent;
import com.marcomoretta.dungeondesk.event.DiceRolledEvent;
import com.marcomoretta.dungeondesk.event.GameEvent;
import com.marcomoretta.dungeondesk.event.GameEventStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameWebSocketHandlerTest {

    @Mock
    private WebSocketSession webSocketSession;
    @Mock
    private GameEventStream gameEventStream;
    @Mock
    private CommandQueue commandQueue;

    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    private GameState gameState;
    private GameWebSocketHandler handler;

    private AuthSession masterSession;
    private AuthSession playerSession;

    @BeforeEach
    void setup() {
        // Arrange
        gameState = new GameState();
        handler = new GameWebSocketHandler(jsonMapper, gameEventStream, gameState, commandQueue);

        masterSession = AuthSession.builder()
                .loginType(LoginType.MASTER).userId(1L).displayName("DM").build();

        playerSession = AuthSession.builder()
                .loginType(LoginType.PLAYER).playerId(3L)
                .campaignId(2L).displayName("Omega").build();
    }

    @Test
    void afterConnectionEstablished() throws Exception {
        // Arrange
        connectedAs(playerSession);

        // Act
        handler.afterConnectionEstablished(webSocketSession);

        // Assert
        verify(gameEventStream).attach(any(ClientObserver.class));
        verify(webSocketSession, never()).close(any());
    }

    @Test
    void afterConnectionEstablished_withNoSession() throws Exception {
        // Arrange
        when(webSocketSession.getAttributes()).thenReturn(new HashMap<>());

        // Act
        handler.afterConnectionEstablished(webSocketSession);

        // Assert
        verify(webSocketSession).close(CloseStatus.POLICY_VIOLATION);
        verifyNoInteractions(gameEventStream);
    }

    @Test
    void afterConnectionEstablished_sendRecentEvents() throws Exception {
        // Arrange
        connectedAs(playerSession);
        when(webSocketSession.isOpen()).thenReturn(true);

        gameState.addEvent(new ChatEvent("DM", null, "first", Instant.now()));
        gameState.addEvent(new ChatEvent("DM", null, "second", Instant.now()));

        // Act
        handler.afterConnectionEstablished(webSocketSession);

        // Assert
        verify(webSocketSession, times(2)).sendMessage(any(TextMessage.class)); // Catch up chat messages when logging in late
    }

    @Test
    void afterConnectionEstablished_sendTurnOrder() throws Exception {
        // Arrange
        connectedAs(playerSession);
        when(webSocketSession.isOpen()).thenReturn(true);

        gameState.setTurnOrder(List.of(2L, 1L));

        // Act
        handler.afterConnectionEstablished(webSocketSession);

        // Assert
        verify(webSocketSession).sendMessage(any(TextMessage.class)); // Catch up turn order when logging in late
    }

    @Test
    void handleTextMessage() throws Exception {
        // Arrange
        connectedAs(masterSession);

        // Act
        handler.handleTextMessage(webSocketSession,
                new TextMessage("{\"type\":\"CHAT\",\"text\":\"Hello\",\"recipient\":null}"));

        // Assert
        verify(commandQueue).submit(any(ChatCommand.class));
    }

    @Test
    void handleTextMessage_onlyMasterCanWhisper() throws Exception {
        // Arrange
        connectedAs(playerSession);

        // Act
        handler.handleTextMessage(webSocketSession,
                new TextMessage("{\"type\":\"CHAT\",\"text\":\"Psst\",\"recipient\":\"Toad\"}"));

        // Assert
        ChatEvent event = (ChatEvent) executeSubmittedCommand();
        assertNull(event.recipientName());
    }

    @Test
    void handleTextMessage_onlyMasterCanHideRoll() throws Exception {
        // Arrange
        connectedAs(playerSession);

        // Act
        handler.handleTextMessage(webSocketSession,
                new TextMessage("{\"type\":\"ROLL_DICE\",\"diceType\":\"D20\",\"hidden\":true}"));

        // Assert
        DiceRolledEvent event = (DiceRolledEvent) executeSubmittedCommand();
        assertFalse(event.hidden());
    }

    @Test
    void handleTextMessage_hiddenRoll() throws Exception {
        // Arrange
        connectedAs(masterSession);

        // Act
        handler.handleTextMessage(webSocketSession,
                new TextMessage("{\"type\":\"ROLL_DICE\",\"diceType\":\"D20\",\"hidden\":true}"));

        // Assert
        DiceRolledEvent event = (DiceRolledEvent) executeSubmittedCommand();
        assertTrue(event.hidden());
    }

    @Test
    void handleTextMessage_missingHiddenField() throws Exception {
        // Arrange
        connectedAs(masterSession);

        // Act
        handler.handleTextMessage(webSocketSession,
                new TextMessage("{\"type\":\"CHAT\",\"text\":\"Hello\"}"));

        // Assert
        verify(commandQueue).submit(any(ChatCommand.class));
    }

    @Test
    void handleTextMessage_malformedMessage() throws Exception {
        // Arrange
        connectedAs(masterSession);

        // Act
        handler.handleTextMessage(webSocketSession, new TextMessage("You are not json, who are you?"));

        // Assert
        verifyNoInteractions(commandQueue);
        verify(webSocketSession, never()).close(any()); // Not closing connection on bad message
    }

    @Test
    void handleTextMessage_unknownType() throws Exception {
        // Arrange
        connectedAs(masterSession);

        // Act
        handler.handleTextMessage(webSocketSession,
                new TextMessage("{\"type\":\"NICE_TRY\"}"));

        // Assert
        verifyNoInteractions(commandQueue);
    }

    @Test
    void afterConnectionClosed() throws Exception {
        // Arrange
        connectedAs(playerSession);
        handler.afterConnectionEstablished(webSocketSession);

        // Act
        handler.afterConnectionClosed(webSocketSession, CloseStatus.NORMAL);

        // Assert
        verify(gameEventStream).detach(any(ClientObserver.class));
    }


    // ----- HELPERS -----

    // Reproduces what WebSocketInterceptor leaves in the attributes at handshake
    private void connectedAs(AuthSession authSession) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(AuthInterceptor.SESSION_ATTRIBUTE, authSession);

        when(webSocketSession.getAttributes()).thenReturn(attributes);
        lenient().when(webSocketSession.getId()).thenReturn("session-1");
    }

    // Commands have no getters so the only way to read what was built is to run them
    private GameEvent executeSubmittedCommand() {
        ArgumentCaptor<Command> captor = ArgumentCaptor.forClass(Command.class);
        verify(commandQueue).submit(captor.capture());

        return captor.getValue().execute(gameState);
    }
}