package com.marcomoretta.dungeondesk.event;

import java.time.Instant;

/**
 * Event raised when a participant updates certain fields of the character sheet
 *
 * @param senderName The display name of the sender
 * @param sheetId    The id of the changed sheet
 * @param currentHp      The new HP value
 * @param sentAt     Send time of the event
 */
public record SheetChangedEvent(
        String senderName,
        Long sheetId,
        int currentHp,
        Instant sentAt
) implements GameEvent {
}
