package com.marcomoretta.dungeondesk.domain.dto;

import lombok.Builder;

/**
 * Dto to expose summary fields of the sheets to be seen in the main view
 *
 * @param sheetType       CHARACTER or ENEMY
 * @param name            Name of the character
 */
@Builder
public record SheetSummaryDto(
        String sheetType,
        Long sheetId,
        String name,
        Long playerId,
        String characterClass,
        String species,
        int currentHp,
        int maxHp
) {
}
