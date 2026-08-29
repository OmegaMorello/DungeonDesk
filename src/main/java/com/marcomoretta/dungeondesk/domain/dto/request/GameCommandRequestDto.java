package com.marcomoretta.dungeondesk.domain.dto.request;

import com.marcomoretta.dungeondesk.command.DiceType;

/**
 * Dto to share game commands through the websocket
 *
 * @param type      Type of command
 * @param recipient Recipient
 * @param text      Optional text
 * @param diceType  Optional Dice type
 * @param hidden    Optional hidden for dice rolls
 */
public record GameCommandRequestDto(
        String type,
        String recipient,
        String text,
        DiceType diceType,
        Boolean hidden
) {
}
