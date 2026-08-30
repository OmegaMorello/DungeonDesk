package com.marcomoretta.dungeondesk.command;

import com.marcomoretta.dungeondesk.event.DiceRolledEvent;
import com.marcomoretta.dungeondesk.event.GameEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class RollDiceCommandTest {

    private GameState gameState;

    @BeforeEach
    void setup() {
        // Arrange
        gameState = new GameState();
    }

    @Test
    void execute() {
        // Arrange
        Random random = new Random() {
            @Override
            public int nextInt(int bound) {
                return 19; // Override the result to 19 for tests
            }
        };

        RollDiceCommand command = new RollDiceCommand("DM", DiceType.D20, false, random);

        // Act
        GameEvent event = command.execute(gameState);
        DiceRolledEvent rolled = (DiceRolledEvent) event;

        // Assert
        assertEquals(20, rolled.result()); // Add 1 to random
        assertEquals(DiceType.D20, rolled.diceType());
        assertFalse(rolled.hidden());
    }

    @Test
    void execute_stayInsideDiceRange() {
        // Arrange
        RollDiceCommand command = new RollDiceCommand("DM", DiceType.D6, false, new Random());

        // Act - Assert
        for (int i = 0; i < 1000; i++) {
            int result = ((DiceRolledEvent) command.execute(gameState)).result();
            assertTrue(result >= 1 && result <= 6);
        }
    }

    @Test
    void execute_hiddenRoll() {
        // Arrange
        RollDiceCommand command = new RollDiceCommand("DM", DiceType.D20, true, new Random());

        // Act
        DiceRolledEvent rolled = (DiceRolledEvent) command.execute(gameState);

        // Assert
        assertTrue(rolled.hidden());
        assertEquals(1, gameState.recentEvents().size());
    }
}