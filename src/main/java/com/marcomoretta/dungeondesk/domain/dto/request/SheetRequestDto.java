package com.marcomoretta.dungeondesk.domain.dto.request;

import com.marcomoretta.dungeondesk.domain.entity.Ability;
import com.marcomoretta.dungeondesk.domain.entity.Skill;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Set;

/**
 * Dto needed to pass Sheet creation and update fields, which are the same
 * The sheet type comes from the endpoint, so an update cannot change it
 *
 * @param playerId CHARACTER only, null leaves the sheet unassigned
 */
public record SheetRequestDto(
        @NotBlank(message = EMPTY_NAME)
        @Size(max = 255, message = NAME_TOO_LONG)
        String name,

        @NotNull(message = "Campaign is required")
        Long campaignId,

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
        List<SpellSlotRequestDto> spellSlots,

        @Valid
        List<AttackRequestDto> attacks,

        @Size(max = 4000, message = NOTES_TOO_LONG)
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
    /**
     * Trims (strips) the name during construction
     */
    public SheetRequestDto {
        if (name != null) name = name.strip();
    }

    private static final String EMPTY_NAME = "A sheet needs a name";
    private static final String NAME_TOO_LONG = "Name must be at most 255 characters long";
    private static final String NOTES_TOO_LONG = "Notes cannot exceed 4000 characters";
}
