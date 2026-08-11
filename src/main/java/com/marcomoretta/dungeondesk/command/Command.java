package com.marcomoretta.dungeondesk.command;

import com.marcomoretta.dungeondesk.event.GameEvent;

public interface Command {
    GameEvent execute(GameState gameState);
}
