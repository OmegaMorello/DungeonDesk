package com.marcomoretta.dungeondesk.domain.dto.request;

import com.marcomoretta.dungeondesk.domain.entity.Ability;
import jakarta.validation.constraints.NotBlank;

/**
 * Dto needed to pass Attack fields. Only the inputs, the totals are derived
 *
 * @param name       Weapon or action name
 * @param ability    The ability it is based on, null when it adds none
 * @param proficient Whether the creature is proficient with it
 * @param magicBonus Bonus of a magic weapon
 * @param damageDie  The die alone, without modifiers
 * @param damageType Slashing, fire and so on
 */
public record AttackRequestDto(
        @NotBlank(message = "An attack needs a name")
        String name,
        Ability ability,
        boolean proficient,
        int magicBonus,
        String damageDie,
        String damageType
) {
}
