package com.marcomoretta.dungeondesk.event;

/**
 * Simple Observer interface
 *
 * @param <E> The event to observe
 */
public interface Observer<E> {
    /**
     * Acts when an event is received
     *
     * @param event The observed event
     */
    void onEvent(E event);
}
