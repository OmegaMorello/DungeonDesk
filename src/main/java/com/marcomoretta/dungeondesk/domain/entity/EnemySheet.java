package com.marcomoretta.dungeondesk.domain.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Enemy Sheet: the sheet of an enemy or non playing character
 * Belongs to the Dungeon Master library, so it can be reused across campaigns
 * Every creature owns its own sheet: five goblins are five sheets
 */
@Entity
@DiscriminatorValue("ENEMY")
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class EnemySheet extends GenericSheet {

    // Challenge rating. Free text to be filled by the DM
    private String challengeRating;

    // Beast, dragon, fiend, humanoid, undead etc.
    private String creatureType;

    // Experience awarded for defeating the creature, read from its challenge rating
    private int experienceReward;
}
