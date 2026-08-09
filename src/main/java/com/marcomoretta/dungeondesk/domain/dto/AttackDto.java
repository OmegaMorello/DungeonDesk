package com.marcomoretta.dungeondesk.domain.dto;

import com.marcomoretta.dungeondesk.domain.entity.Ability;

/**
 * Dto to expose fields of Attack, with the derived totals
 *
 * @param name           Weapon or action name
 * @param ability        The ability it is based on, null when it adds none
 * @param proficient     Whether the creature is proficient with it
 * @param magicBonus     Bonus of a magic weapon
 * @param damageDie      The die alone, without modifiers
 * @param damageType     Slashing, fire and so on
 * @param attackBonus    Derived total for the d20 roll
 * @param damageModifier Derived flat bonus on the damage die
 */
public record AttackDto(
        String name,
        Ability ability,
        boolean proficient,
        int magicBonus,
        String damageDie,
        String damageType,
        int attackBonus,
        int damageModifier
) {
}
