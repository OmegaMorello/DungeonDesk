package com.marcomoretta.dungeondesk.command;

import com.marcomoretta.dungeondesk.event.GameEvent;

/**
 * Command interface. Implemented by every action the players need to issue different commands
 */
public interface Command {
    /**
     * Executes the command and saves it to the session game state
     * @param gameState The volatile/session game state
     * @return A new game event
     */
    GameEvent execute(GameState gameState);

    // Messages accepted from the client (see GameWebSocketHandler)
    String CHAT = "CHAT";
    String ROLL_DICE = "ROLL_DICE";
}
