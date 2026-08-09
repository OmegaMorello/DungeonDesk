package com.marcomoretta.dungeondesk.domain.dto;

import com.marcomoretta.dungeondesk.domain.entity.Ability;
import com.marcomoretta.dungeondesk.domain.entity.Skill;

import lombok.Builder;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Dto to expose fields of both kinds of sheet
 * Flat and not polymorphic: the client switches on sheetType to pick the form to render
 * Carries the derived values too, so the client does not reimplement the rules
 *
 * @param sheetType          CHARACTER or ENEMY
 * @param abilityModifiers   Derived, one per ability
 * @param savingThrows       Derived, proficiency already applied
 * @param skillModifiers     Derived, proficiency and expertise already applied
 * @param initiative         Derived
 * @param passivePerception  Derived
 * @param spellSaveDc        Derived, null when the creature does not cast
 * @param spellAttackBonus   Derived, null when the creature does not cast
 * @param playerId           CHARACTER only, null when unassigned
 * @param hitDiceTotal       CHARACTER only, derived
 * @param challengeRating    ENEMY only
 */
@Builder
public record SheetDto(
        String sheetType,
        Long sheetId,
        String name,

        int armorClass,
        int maxHp,
        int currentHp,
        int speed,

        int strength,
        int dexterity,
        int constitution,
        int intelligence,
        int wisdom,
        int charisma,

        int proficiencyBonus,

        Set<Skill> skillProficiencies,
        Set<Skill> skillExpertise,
        Set<Ability> savingThrowProficiencies,

        Ability spellcastingAbility,
        List<SpellSlotDto> spellSlots,
        List<AttackDto> attacks,

        String notes,

        // Derived
        Map<Ability, Integer> abilityModifiers,
        Map<Ability, Integer> savingThrows,
        Map<Skill, Integer> skillModifiers,
        int initiative,
        int passivePerception,
        Integer spellSaveDc,
        Integer spellAttackBonus,

        // CHARACTER only
        Long playerId,
        Integer level,
        String characterClass,
        String species,
        Integer experiencePoints,
        Integer hitDiceSize,
        Integer hitDiceRemaining,
        Integer hitDiceTotal,
        Integer deathSaveSuccesses,
        Integer deathSaveFailures,
        Integer exhaustion,
        Boolean inspiration,

        // ENEMY only
        String challengeRating,
        String creatureType,
        Integer experienceReward
) {
}
