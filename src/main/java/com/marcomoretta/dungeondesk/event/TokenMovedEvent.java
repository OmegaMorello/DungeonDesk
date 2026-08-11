package com.marcomoretta.dungeondesk.event;

import java.time.Instant;

/**
 * Event raised when a participant moves a token
 *
 * @param senderName The display name of the sender
 * @param tokenId    The id of the moved token
 * @param posX      X coordinate
 * @param posY      Y coordinate
 * @param sentAt     Send time of the event
 */
public record TokenMovedEvent(
        String senderName,
        Long tokenId,
        int posX,
        int posY,
        Instant sentAt
) implements GameEvent {
}
