package com.marcomoretta.dungeondesk.event;

import java.time.Instant;

/**
 * Event raised when a token is removed from the map
 *
 * @param senderName The display name of the sender
 * @param tokenId    The id of the moved token
 * @param sentAt     Send time of the event
 */
public record TokenRemovedEvent(
        String senderName,
        Long tokenId,
        Instant sentAt
) implements GameEvent {
}
