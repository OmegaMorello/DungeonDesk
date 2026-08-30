package com.marcomoretta.dungeondesk.command;

import com.marcomoretta.dungeondesk.event.ChatEvent;
import com.marcomoretta.dungeondesk.event.GameEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChatCommandTest {

    private GameState gameState;

    @BeforeEach
    void setup() {
        // Arrange
        gameState = new GameState();
    }

    @Test
    void execute() {
        // Arrange
        ChatCommand command = new ChatCommand("DM", null, "Hello fellows");

        // Act
        GameEvent event = command.execute(gameState);

        // Assert
        assertInstanceOf(ChatEvent.class, event);
        assertEquals("Hello fellows", ((ChatEvent) event).text());
        assertEquals(1, gameState.recentEvents().size());
    }

    @Test
    void execute_whisper() {
        // Arrange
        ChatCommand command = new ChatCommand("DM", "Omega", "Only for you");

        // Act
        GameEvent event = command.execute(gameState);

        // Assert
        assertEquals("Omega", ((ChatEvent) event).recipientName());
        assertTrue(gameState.recentEvents().isEmpty()); // Hidden/direct messages are not saved
    }
}