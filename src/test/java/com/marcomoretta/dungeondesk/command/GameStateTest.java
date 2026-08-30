package com.marcomoretta.dungeondesk.command;

import com.marcomoretta.dungeondesk.event.ChatEvent;
import com.marcomoretta.dungeondesk.event.GameEvent;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameStateTest {

    private final GameState gameState = new GameState();

    @Test
    void addEvent() {
        // Arrange
        ChatEvent first = new ChatEvent("DM", null, "one", Instant.now());
        ChatEvent second = new ChatEvent("DM", null, "two", Instant.now());

        // Act
        gameState.addEvent(first);
        gameState.addEvent(second);

        // Assert
        assertEquals(List.of(first, second), gameState.recentEvents());
    }

    @Test
    void addEvent_dropTheOldest() {
        // Arrange
        for (int i = 0; i < 250; i++)
            gameState.addEvent(new ChatEvent("DM", null, "e" + i, Instant.now()));

        // Act
        List<GameEvent> events = gameState.recentEvents();

        // Assert
        assertEquals(200, events.size());
        // Events before the 50th have been trimmed
        assertEquals("e50", ((ChatEvent) events.getFirst()).text());
    }

    @Test
    void recentEvents() {
        // Arrange
        gameState.addEvent(new ChatEvent("DM", null, "uno", Instant.now()));

        // Act
        gameState.recentEvents().clear();

        // Assert
        assertEquals(1, gameState.recentEvents().size());
    }

    @Test
    void turnOrder() {
        // Arrange
        assertTrue(gameState.turnOrder().isEmpty());

        // Act
        gameState.setTurnOrder(List.of(2L, 1L));

        // Assert
        assertEquals(List.of(2L, 1L), gameState.turnOrder());
    }
}