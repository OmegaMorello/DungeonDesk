package com.marcomoretta.dungeondesk.command;

import com.marcomoretta.dungeondesk.event.GameEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Saves the session-only volatile game state
 */
@Component
public class GameState {
    private static final int MAX_QUEUE_SIZE = 200;
    private final Deque<GameEvent> gameEvents = new ArrayDeque<>();
    private List<Long> turnOrder = List.of();

    /**
     * Add an event and ensure the queue is less than the maximum
     *
     * @param event The event to add
     */
    public synchronized void addEvent(GameEvent event) {
        gameEvents.addLast(event);
        while (gameEvents.size() > MAX_QUEUE_SIZE) gameEvents.pollFirst();
    }

    /**
     * Get the recent events list
     *
     * @return The recent events list
     */
    public synchronized List<GameEvent> recentEvents() {
        return new ArrayList<>(gameEvents);
    }


    /**
     * Sets the turn order. Synchronized to avoid being read while changing
     *
     * @param sheetIds The turn-ordered sheet ids list
     */
    public synchronized void setTurnOrder(List<Long> sheetIds) {
        turnOrder = List.copyOf(sheetIds);
    }

    /**
     * Gets the turn order. Synchronized to avoid reading while being changed
     *
     * @return The turn-ordered sheet ids list
     */
    public synchronized List<Long> turnOrder() {
        return turnOrder;
    }
}
