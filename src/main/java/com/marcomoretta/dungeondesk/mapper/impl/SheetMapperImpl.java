package com.marcomoretta.dungeondesk.mapper.impl;

import com.marcomoretta.dungeondesk.domain.dto.AttackDto;
import com.marcomoretta.dungeondesk.domain.dto.SheetDto;
import com.marcomoretta.dungeondesk.domain.dto.SheetSummaryDto;
import com.marcomoretta.dungeondesk.domain.dto.SpellSlotDto;
import com.marcomoretta.dungeondesk.domain.dto.request.SheetRequestDto;
import com.marcomoretta.dungeondesk.domain.entity.*;
import com.marcomoretta.dungeondesk.domain.request.SheetRequest;
import com.marcomoretta.dungeondesk.mapper.SheetMapper;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Mapper for the sheets to and from Dto
 * The concrete class only decides which optional block of the flat dto is filled in
 */
@Component
public class SheetMapperImpl implements SheetMapper {

    @Override
    public SheetRequest fromDto(SheetRequestDto dto, Long sheetId, Long ownerId) {
        return new SheetRequest(
                sheetId,
                ownerId,
                dto.campaignId(),
                dto.name(),
                dto.armorClass(), dto.maxHp(), dto.currentHp(), dto.speed(),
                dto.strength(), dto.dexterity(), dto.constitution(),
                dto.intelligence(), dto.wisdom(), dto.charisma(),
                dto.proficiencyBonus(),
                orEmpty(dto.skillProficiencies()),
                orEmpty(dto.skillExpertise()),
                orEmpty(dto.savingThrowProficiencies()),
                dto.spellcastingAbility(),
                dto.spellSlots() == null ? List.of() : dto.spellSlots().stream()
                        .map(s -> SpellSlot.builder()
                                .level(s.level()).total(s.total()).used(s.used()).build())
                        .toList(),
                dto.attacks() == null ? List.of() : dto.attacks().stream()
                        .map(a -> Attack.builder()
                                .name(a.name()).ability(a.ability()).proficient(a.proficient())
                                .magicBonus(a.magicBonus()).damageDie(a.damageDie())
                                .damageType(a.damageType()).build())
                        .toList(),
                dto.notes(),
                dto.playerId(), dto.level(), dto.characterClass(), dto.species(),
                dto.experiencePoints(), dto.hitDiceSize(), dto.hitDiceRemaining(),
                dto.deathSaveSuccesses(), dto.deathSaveFailures(), dto.exhaustion(),
                dto.inspiration(),
                dto.challengeRating(), dto.creatureType(), dto.experienceReward()
        );
    }

    @Override
    public SheetDto toDto(GenericSheet sheet) {

        SheetDto.SheetDtoBuilder builder = SheetDto.builder()
                .sheetId(sheet.getSheetId())
                .name(sheet.getName())

                .armorClass(sheet.getArmorClass())
                .maxHp(sheet.getMaxHp())
                .currentHp(sheet.getCurrentHp())
                .speed(sheet.getSpeed())

                .strength(sheet.getStrength())
                .dexterity(sheet.getDexterity())
                .constitution(sheet.getConstitution())
                .intelligence(sheet.getIntelligence())
                .wisdom(sheet.getWisdom())
                .charisma(sheet.getCharisma())

                .proficiencyBonus(sheet.getProficiencyBonus())

                .skillProficiencies(sheet.getSkillProficiencies())
                .skillExpertise(sheet.getSkillExpertise())
                .savingThrowProficiencies(sheet.getSavingThrowProficiencies())

                .spellcastingAbility(sheet.getSpellcastingAbility())
                .spellSlots(toSpellSlotDtoList(sheet))
                .attacks(toAttackDtoList(sheet))

                .notes(sheet.getNotes())

                // Derived once here, so the client does not reimplement the rules
                .abilityModifiers(modifiersByAbility(sheet))
                .savingThrows(savingThrowsByAbility(sheet))
                .skillModifiers(modifiersBySkill(sheet))
                .initiative(sheet.getInitiative())
                .passivePerception(sheet.getPassivePerception())
                .spellSaveDc(sheet.getSpellSaveDc())
                .spellAttackBonus(sheet.getSpellAttackBonus());

        // Only the block matching the concrete class is filled in, the other stays null
        if (sheet instanceof CharacterSheet character) {
            builder.sheetType("CHARACTER")
                    .playerId(character.getPlayer() == null ? null : character.getPlayer().getPlayerId())
                    .level(character.getLevel())
                    .characterClass(character.getCharacterClass())
                    .species(character.getSpecies())
                    .experiencePoints(character.getExperiencePoints())
                    .hitDiceSize(character.getHitDiceSize())
                    .hitDiceRemaining(character.getHitDiceRemaining())
                    .hitDiceTotal(character.getHitDiceTotal())
                    .deathSaveSuccesses(character.getDeathSaveSuccesses())
                    .deathSaveFailures(character.getDeathSaveFailures())
                    .exhaustion(character.getExhaustion())
                    .inspiration(character.isInspiration());
        }

        if (sheet instanceof EnemySheet enemy) {
            builder.sheetType("ENEMY")
                    .challengeRating(enemy.getChallengeRating())
                    .creatureType(enemy.getCreatureType())
                    .experienceReward(enemy.getExperienceReward());
        }

        return builder.build();
    }

    @Override
    public List<SheetDto> toDtoList(List<GenericSheet> sheetList) {
        return sheetList.stream().map(this::toDto).toList();
    }

    @Override
    public SheetSummaryDto toSummaryDto(GenericSheet sheet) {
        SheetSummaryDto.SheetSummaryDtoBuilder builder = SheetSummaryDto.builder()
                .sheetId(sheet.getSheetId())
                .name(sheet.getName())
                .currentHp(sheet.getCurrentHp())
                .maxHp(sheet.getMaxHp());

        if (sheet instanceof CharacterSheet) {
            builder
                    .sheetType(CharacterSheet.class.getSimpleName())
                    .playerId(((CharacterSheet) sheet).getPlayer() == null ? null : ((CharacterSheet) sheet).getPlayer().getPlayerId())
                    .characterClass(((CharacterSheet) sheet).getCharacterClass())
                    .species(((CharacterSheet) sheet).getSpecies());
        }

        if (sheet instanceof EnemySheet) {
            builder
                    .sheetType(EnemySheet.class.getSimpleName());
        }

        return builder.build();
    }

    @Override
    public List<SheetSummaryDto> toSummaryDtoList(List<GenericSheet> sheetList) {
        return sheetList.stream().map(this::toSummaryDto).toList();
    }

    // Converts the spells to a list DTO for easier usage
    private List<SpellSlotDto> toSpellSlotDtoList(GenericSheet sheet) {
        return sheet.getSpellSlots().stream()
                .map(s -> new SpellSlotDto(s.getLevel(), s.getTotal(), s.getUsed(), s.getRemaining()))
                .toList();
    }

    // Converts the spells to a list DTO for easier usage. Includes bonus and damage calculations
    private List<AttackDto> toAttackDtoList(GenericSheet sheet) {
        return sheet.getAttacks().stream()
                .map(a -> new AttackDto(
                        a.getName(), a.getAbility(), a.isProficient(), a.getMagicBonus(),
                        a.getDamageDie(), a.getDamageType(),
                        sheet.getAttackBonus(a), sheet.getDamageModifier(a)))
                .toList();
    }

    // EnumMap keeps the declaration order, so the client renders the sheet as printed
    // Gets the ability modifiers map
    private Map<Ability, Integer> modifiersByAbility(GenericSheet sheet) {
        Map<Ability, Integer> map = new EnumMap<>(Ability.class);
        for (Ability ability : Ability.values()) map.put(ability, sheet.getAbilityModifier(ability));
        return map;
    }

    // EnumMap keeps the declaration order, so the client renders the sheet as printed
    // Gets the saving throws map
    private Map<Ability, Integer> savingThrowsByAbility(GenericSheet sheet) {
        Map<Ability, Integer> map = new EnumMap<>(Ability.class);
        for (Ability ability : Ability.values()) map.put(ability, sheet.getSavingThrow(ability));
        return map;
    }

    // EnumMap keeps the declaration order, so the client renders the sheet as printed
    // Gets the skills modifiers map
    private Map<Skill, Integer> modifiersBySkill(GenericSheet sheet) {
        Map<Skill, Integer> map = new EnumMap<>(Skill.class);
        for (Skill skill : Skill.values()) map.put(skill, sheet.getSkillModifier(skill));
        return map;
    }

    // Jackson leaves an absent JSON field null, and the service needs a collection to copy
    private <T> Set<T> orEmpty(Set<T> set) {
        return set == null ? Set.of() : set;
    }
}
