package com.marcomoretta.dungeondesk.domain.request;

import com.marcomoretta.dungeondesk.domain.entity.Ability;
import com.marcomoretta.dungeondesk.domain.entity.Skill;
import com.marcomoretta.dungeondesk.domain.entity.Attack;
import com.marcomoretta.dungeondesk.domain.entity.SpellSlot;

import java.util.List;
import java.util.Set;

/**
 * The request to create or update a sheet in the service layer. To be used through a dto
 *
 * @param sheetId Null when creating
 * @param ownerId The Dungeon Master the sheet belongs to
 */
public record SheetRequest(
        Long sheetId,
        Long ownerId,
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
        List<SpellSlot> spellSlots,
        List<Attack> attacks,
        String notes,
        // CHARACTER only
        Long playerId,
        Integer level,
        String characterClass,
        String species,
        Integer experiencePoints,
        Integer hitDiceSize,
        Integer hitDiceRemaining,
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
