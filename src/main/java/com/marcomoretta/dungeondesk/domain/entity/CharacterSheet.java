package com.marcomoretta.dungeondesk.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Character Sheet: the sheet of a player character
 * May exist without a player, so it can be prepared before being assigned
 */
@Entity
@DiscriminatorValue("CHARACTER")
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class CharacterSheet extends GenericSheet {

    // At most one player
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", unique = true)
    @ToString.Exclude
    private Player player;

    private int level;

    private String characterClass;

    private String species;

    private int experiencePoints;

    // ---- Hit dice, spent on a short rest and recovered on a long one --------------

    // Size of the die granted by the class, the 8 of a d8
    private int hitDiceSize;

    // How many are left to spend: the total equals the level
    private int hitDiceRemaining;

    // ---- Death saves, reset as soon as the character is stable or healed ----------

    private int deathSaveSuccesses;

    private int deathSaveFailures;

    // Exhaustion level, from 0 to 6. Six means death
    private int exhaustion;

    private boolean inspiration;

    // ==== Derived values ===========================================================

    /**
     * Total hit dice available at this level, one per level
     *
     * @return The hit dice pool size
     */
    public int getHitDiceTotal() {
        return level;
    }

    /**
     * A character drops at zero hit points and dies after three failed death saves
     *
     * @return True when the character is unconscious and rolling death saves
     */
    public boolean isUnconscious() {
        return getCurrentHp() <= 0;
    }
}
