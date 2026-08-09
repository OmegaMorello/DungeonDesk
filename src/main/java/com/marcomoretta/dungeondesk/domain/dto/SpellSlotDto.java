package com.marcomoretta.dungeondesk.domain.dto;

/**
 * Dto to expose fields of SpellSlot
 *
 * @param level     Spell level, 1 to 9
 * @param total     Slots granted
 * @param used      Slots already spent
 * @param remaining Derived difference
 */
public record SpellSlotDto(
        int level,
        int total,
        int used,
        int remaining
) {
}
