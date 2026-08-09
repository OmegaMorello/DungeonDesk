package com.marcomoretta.dungeondesk.domain.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Sheet of an enemy or non playing character, belonging to the Dungeon Master library
 * rather than to a campaign, so that the same creature can be reused across campaigns.
 * Every creature on the map owns its own sheet: placing five goblins creates five
 * sheets, because current hit points are per creature.
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
