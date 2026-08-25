package com.marcomoretta.dungeondesk.event;

import java.time.Instant;

/**
 * Event raised when a participant updates certain fields of the character sheet
 *
 * @param senderName The display name of the sender
 * @param sheetId    The id of the changed sheet
 * @param name       The name of the player
 * @param currentHp  The new HP value
 * @param maxHp      The max HP value
 * @param sentAt     Send time of the event
 */
public record SheetChangedEvent(
        String senderName,
        Long sheetId,
        String name,
        int currentHp,
        int maxHp,
        Instant sentAt
) implements GameEvent {
}
