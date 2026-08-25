package com.marcomoretta.dungeondesk.command;

import com.marcomoretta.dungeondesk.event.DiceRolledEvent;
import com.marcomoretta.dungeondesk.event.GameEvent;

import java.time.Instant;
import java.util.Random;

/**
 * Command to roll dice and publish the result in the session chat
 */
public class RollDiceCommand implements Command{
    private final String senderName;
    private final DiceType diceType;
    private final boolean hidden;
    private final Random random;

    public RollDiceCommand(String senderName, DiceType diceType, boolean hidden, Random random) {
        this.senderName = senderName;
        this.diceType = diceType;
        this.hidden = hidden;
        this.random = random;
    }


    @Override
    public GameEvent execute(GameState gameState) {
        int rollResult = random.nextInt(diceType.getSides()) + 1;
        DiceRolledEvent diceRolledEvent = new DiceRolledEvent(senderName, diceType, rollResult, hidden, Instant.now());
        gameState.addEvent(diceRolledEvent);
        return diceRolledEvent;
    }
}
