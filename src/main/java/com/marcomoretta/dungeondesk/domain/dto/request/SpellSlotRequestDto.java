package com.marcomoretta.dungeondesk.domain.dto.request;

/**
 * Dto needed to pass SpellSlot fields
 *
 * @param level Spell level, 1 to 9
 * @param total Slots granted
 * @param used  Slots already spent
 */
public record SpellSlotRequestDto(
        int level,
        int total,
        int used
) {
}
