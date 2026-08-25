package com.marcomoretta.dungeondesk.websocket;

import com.marcomoretta.dungeondesk.auth.AuthInterceptor;
import com.marcomoretta.dungeondesk.auth.AuthSession;
import com.marcomoretta.dungeondesk.auth.LoginType;
import com.marcomoretta.dungeondesk.command.*;
import com.marcomoretta.dungeondesk.domain.dto.request.GameCommandRequestDto;
import com.marcomoretta.dungeondesk.event.GameEvent;
import com.marcomoretta.dungeondesk.event.GameEventStream;
import com.marcomoretta.dungeondesk.event.TurnOrderChangedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles the game connections
 */
@Slf4j
@Component
public class GameWebSocketHandler extends TextWebSocketHandler {

    private final ConcurrentHashMap<String, ClientObserver> observers = new ConcurrentHashMap<>();
    private final JsonMapper jsonMapper;
    private final GameEventStream gameEventStream;
    private final GameState gameState;
    private final Random random = new Random();
    private final CommandQueue commandQueue;

    public GameWebSocketHandler(JsonMapper jsonMapper, GameEventStream gameEventStream, GameState gameState, CommandQueue commandQueue) {
        this.jsonMapper = jsonMapper;
        this.gameEventStream = gameEventStream;
        this.gameState = gameState;
        this.commandQueue = commandQueue;
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        AuthSession authSession = getAuthSession(session);
        if (authSession == null) {
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }

        // Saves the observer in a map and attaches it to the observable game event stream
        ClientObserver clientObserver = new ClientObserver(session, authSession, jsonMapper);
        observers.put(session.getId(), clientObserver);
        gameEventStream.attach(clientObserver);

        List<GameEvent> gameEvents = gameState.recentEvents();
        List<Long> turnOrder = gameState.turnOrder();

        // Sends the recent event to allow the client to catch up
        for (GameEvent gameEvent : gameEvents) clientObserver.onEvent(gameEvent);

        // If the turn order was set prior to the client connection, it is sent here to allow the client to catch up
        if (!turnOrder.isEmpty())
            clientObserver.onEvent(new TurnOrderChangedEvent("first connection", turnOrder, Instant.now()));
    }


    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        AuthSession authSession = getAuthSession(session);
        if (authSession == null) {
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }

        GameCommandRequestDto gameCommandRequestDto;
        try {
            gameCommandRequestDto = jsonMapper.readValue(message.getPayload(), GameCommandRequestDto.class);
        } catch (Exception exception) {
            // Prevents the socket connection to be closed
            log.warn("Malformed message: {}", message.getPayload(), exception);
            return;
        }

        Command command = null;

        switch (gameCommandRequestDto.type()) {

            case Command.CHAT:
                // A DM may choose to send the message only to a specific recipient.
                // A player message is always sent to everybody
                String recipient = authSession.loginType().equals(LoginType.MASTER)
                        ? gameCommandRequestDto.recipient()
                        : null;

                command = new ChatCommand(
                        authSession.displayName(),
                        recipient,
                        gameCommandRequestDto.text());
                break;

            case Command.ROLL_DICE:
                // Only DM can hide dice rolls
                boolean hidden = gameCommandRequestDto.hidden() && authSession.loginType().equals(LoginType.MASTER);

                command = new RollDiceCommand(
                        authSession.displayName(),
                        gameCommandRequestDto.diceType(),
                        hidden,
                        random);
                break;

            default:
                break;
        }

        // The command will be added to the queue, notifying the consumer thread so it can consume it and notify the observers
        if (command != null) commandQueue.submit(command);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        ClientObserver observer = observers.get(session.getId());
        if (observer != null)
            gameEventStream.detach(observer);
    }

    // Reads the auth session through the web socket session attributes
    private AuthSession getAuthSession(WebSocketSession session) {
        Object attribute = session.getAttributes().get(AuthInterceptor.SESSION_ATTRIBUTE);
        return attribute instanceof AuthSession authSession ? authSession : null;
    }

}
