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
}
