package com.marcomoretta.dungeondesk.command;

import com.marcomoretta.dungeondesk.event.ChatEvent;
import com.marcomoretta.dungeondesk.event.GameEvent;

import java.time.Instant;

public class ChatCommand implements Command {
    private final String senderName;
    private final String text;

    public ChatCommand(String senderName, String text) {
        this.senderName = senderName;
        this.text = text;
    }

    @Override
    public GameEvent execute(GameState gameState) {
        ChatEvent chatEvent = new ChatEvent(senderName, text, Instant.now());
        gameState.addEvent(chatEvent);
        return chatEvent;
    }
}
