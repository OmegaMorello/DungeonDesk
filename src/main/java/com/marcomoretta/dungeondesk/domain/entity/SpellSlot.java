package com.marcomoretta.dungeondesk.domain.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * Spell slots of a single spell level.
 */
@Embeddable
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class SpellSlot {

    private int level;
    private int total;
    private int used;

    /**
     * Gets the remaining spell slots
     *
     * @return How many slots of this level are still available
     */
    public int getRemaining() {
        return Math.max(0, total - used);
    }
}
