package com.marcomoretta.dungeondesk.event;

import java.time.Instant;
import java.util.List;

/**
 * Event raised when the DM rolls or sets the initiative turn order
 *
 * @param senderName The display name of the sender
 * @param sheetIds   The sheets ids
 * @param sentAt     Send time of the event
 */
public record TurnOrderChangedEvent(
        String senderName,
        List<Long> sheetIds,
        Instant sentAt
) implements GameEvent {
}
