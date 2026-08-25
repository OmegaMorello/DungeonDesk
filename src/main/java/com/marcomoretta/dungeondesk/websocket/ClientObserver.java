package com.marcomoretta.dungeondesk.websocket;

import com.marcomoretta.dungeondesk.auth.AuthSession;
import com.marcomoretta.dungeondesk.auth.LoginType;
import com.marcomoretta.dungeondesk.event.ChatEvent;
import com.marcomoretta.dungeondesk.event.DiceRolledEvent;
import com.marcomoretta.dungeondesk.event.GameEvent;
import com.marcomoretta.dungeondesk.event.Observer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;

/**
 * One observer per open connection
 */
@Slf4j
public class ClientObserver implements Observer<GameEvent> {

    private final WebSocketSession webSocketSession;
    private final AuthSession authSession;
    private final JsonMapper jsonMapper;

    public ClientObserver(WebSocketSession webSocketSession, AuthSession authSession, JsonMapper jsonMapper) {
        this.webSocketSession = webSocketSession;
        this.authSession = authSession;
        this.jsonMapper = jsonMapper;
    }


    @Override
    public void onEvent(GameEvent event) {

        TextMessage textMessage = new TextMessage(jsonMapper.writeValueAsString(event));
        try {
            sendMessage(textMessage);
        } catch (IOException exception) {
            log.warn("Failed to deliver event to session {}", webSocketSession.getId(), exception);
        }
    }

    // Sends a message and holds the lock to prevent another thread to send a message while one is being processed
    private synchronized void sendMessage(TextMessage textMessage) throws IOException {
        if (webSocketSession.isOpen()) webSocketSession.sendMessage(textMessage);
    }

    // DM sees everything
    private boolean isVisible(GameEvent event) {

        // Prevents DM dice rolls to be seen by players
        if (event instanceof DiceRolledEvent dice
                && dice.hidden()
                && !authSession.loginType().equals(LoginType.MASTER))
            return false;

        // A player sees only messages sent by them, to them or to the group
        if (event instanceof ChatEvent chat
                && chat.recipientName() != null)
            return chat.recipientName().equals(authSession.displayName())
                    || chat.senderName().equals(authSession.displayName());

        return true;
    }
}

