package com.marcomoretta.dungeondesk.event;

import java.time.Instant;

/**
 * Event raised when a participant sends a message to the session chat
 *
 * @param senderName    The display name of the sender
 * @param recipientName Name of the recipient (null = everyone)
 * @param text          The message content
 * @param sentAt        Send time of the event
 */
public record ChatEvent(
        String senderName,
        String recipientName,
        String text,
        Instant sentAt
) implements GameEvent {
}
