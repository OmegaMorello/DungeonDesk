package com.marcomoretta.dungeondesk.event;

import com.marcomoretta.dungeondesk.command.DiceType;

import java.time.Instant;

/**
 * Event raised when a participant rolls a dice
 *
 * @param senderName The display name of the sender
 * @param diceType   The type of dice enum
 * @param result     The roll result
 * @param hidden     If the roll is hidden for the players (DM roll)
 * @param sentAt     Send time of the event
 */
public record DiceRolledEvent(
        String senderName,
        DiceType diceType,
        int result,
        boolean hidden,
        Instant sentAt
) implements GameEvent {
}
