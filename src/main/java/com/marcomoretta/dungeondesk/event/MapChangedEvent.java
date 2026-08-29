package com.marcomoretta.dungeondesk.event;

import java.time.Instant;

/**
 * Event raised when the map changes
 *
 * @param senderName        The display name of the sender
 * @param backgroundChanged True only if the background has changed
 * @param sentAt            Send time of the event
 */
public record MapChangedEvent(
        String senderName,
        boolean backgroundChanged,
        Instant sentAt
) implements GameEvent {

    public static MapChangedEvent of (String senderName, boolean backgroundChanged) {
        return new MapChangedEvent(senderName, backgroundChanged, Instant.now());
    }
}
