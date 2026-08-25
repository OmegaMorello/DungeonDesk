package com.marcomoretta.dungeondesk.command;

import com.marcomoretta.dungeondesk.event.ChatEvent;
import com.marcomoretta.dungeondesk.event.GameEvent;

import java.time.Instant;

/**
 * Command to publish a message in the session chat
 */
public class ChatCommand implements Command {
    private final String senderName;
    private final String recipientName;
    private final String text;

    public ChatCommand(String senderName, String recipientName, String text) {
        this.senderName = senderName;
        this.recipientName = recipientName;
        this.text = text;
    }

    @Override
    public GameEvent execute(GameState gameState) {
        ChatEvent chatEvent = new ChatEvent(senderName, recipientName, text, Instant.now());
        if (recipientName == null) gameState.addEvent(chatEvent);
        return chatEvent;
    }
}
