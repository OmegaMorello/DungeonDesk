package com.marcomoretta.dungeondesk.mapper.impl;

import com.marcomoretta.dungeondesk.domain.dto.AttackDto;
import com.marcomoretta.dungeondesk.domain.dto.SheetDto;
import com.marcomoretta.dungeondesk.domain.dto.SheetSummaryDto;
import com.marcomoretta.dungeondesk.domain.dto.SpellSlotDto;
import com.marcomoretta.dungeondesk.domain.dto.request.AttackRequestDto;
import com.marcomoretta.dungeondesk.domain.dto.request.SheetRequestDto;
import com.marcomoretta.dungeondesk.domain.dto.request.SpellSlotRequestDto;
import com.marcomoretta.dungeondesk.domain.entity.*;
import com.marcomoretta.dungeondesk.domain.request.SheetRequest;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SheetMapperImplTest {

    private final SheetMapperImpl sheetMapper = new SheetMapperImpl();

    // Arrange
    private final CharacterSheet characterSheet = CharacterSheet.builder()
            .sheetId(1L)
            .name("Omega")
            .armorClass(10).maxHp(30).currentHp(28).speed(9)
            .strength(12).dexterity(10).constitution(14).intelligence(16).wisdom(11).charisma(7)
            .proficiencyBonus(3)
            .skillProficiencies(EnumSet.of(Skill.ARCANA))
            .skillExpertise(EnumSet.of(Skill.HISTORY))
            .savingThrowProficiencies(EnumSet.of(Ability.INTELLIGENCE))
            .spellSlots(Set.of())
            .attacks(Set.of())
            .notes("")
            .level(1).characterClass("Mage").species("Elf").experiencePoints(0)
            .hitDiceSize(8).hitDiceRemaining(1)
            .deathSaveSuccesses(0).deathSaveFailures(0)
            .exhaustion(0)
            .inspiration(true)
            .build();

    private final EnemySheet enemySheet = EnemySheet.builder()
            .sheetId(1L)
            .name("Wurmple")
            .armorClass(10).maxHp(30).currentHp(28).speed(9)
            .strength(12).dexterity(10).constitution(14).intelligence(16).wisdom(11).charisma(7)
            .proficiencyBonus(3)
            .skillProficiencies(EnumSet.of(Skill.NATURE))
            .skillExpertise(EnumSet.of(Skill.PERCEPTION))
            .savingThrowProficiencies(EnumSet.of(Ability.DEXTERITY))
            .spellSlots(Set.of())
            .attacks(Set.of())
            .notes("")
            .challengeRating("1/4").creatureType("Goblin").experienceReward(25)
            .build();


    @Test
    void toDto_characterSheet() {
        // Act
        SheetDto sheetDto = sheetMapper.toDto(characterSheet);

        // Assert
        assertEquals("CHARACTER", sheetDto.sheetType());
        assertEquals("Omega", sheetDto.name());
        assertEquals(1, sheetDto.level());
        assertEquals(Ability.values().length, sheetDto.abilityModifiers().size());
        assertNull(sheetDto.challengeRating());
    }

    @Test
    void toDto_enemySheet() {
        // Act
        SheetDto sheetDto = sheetMapper.toDto(enemySheet);

        // Assert
        assertEquals("ENEMY", sheetDto.sheetType());
        assertEquals("Wurmple", sheetDto.name());
        assertEquals("Goblin", sheetDto.creatureType());
        assertEquals(25, sheetDto.experienceReward());
        assertNull(sheetDto.level());
    }

    @Test
    void toSummaryDto_characterSheet() {
        // Act
        SheetSummaryDto sheetSummaryDto = sheetMapper.toSummaryDto(characterSheet);

        // Assert
        assertEquals("CHARACTER", sheetSummaryDto.sheetType());
        assertEquals("Elf", sheetSummaryDto.species());
        assertEquals(28, sheetSummaryDto.currentHp());
    }

    @Test
    void toSummaryDto_enemySheet() {
        // Act
        SheetSummaryDto sheetSummaryDto = sheetMapper.toSummaryDto(enemySheet);

        // Assert
        assertEquals("ENEMY", sheetSummaryDto.sheetType());
        assertEquals(30, sheetSummaryDto.maxHp());
    }

    @Test
    void toDtoList() {
        // Act
        List<SheetDto> sheetDtoList = sheetMapper.toDtoList(List.of(characterSheet, enemySheet));

        // Assert
        assertEquals(2, sheetDtoList.size());
        assertEquals("CHARACTER", sheetDtoList.getFirst().sheetType());
        assertEquals("ENEMY", sheetDtoList.getLast().sheetType());
    }

    @Test
    void toSummaryDtoList() {
        // Act
        List<SheetSummaryDto> sheetMapperDtoList = sheetMapper.toSummaryDtoList(List.of(characterSheet, enemySheet));

        // Assert
        assertEquals(2, sheetMapperDtoList.size());
        assertEquals("CHARACTER", sheetMapperDtoList.getFirst().sheetType());
        assertEquals("ENEMY", sheetMapperDtoList.getLast().sheetType());
    }

    @Test
    void fromDto() {
        // Arrange
        AttackRequestDto attackRequestDto = new AttackRequestDto("Dual blades",
                Ability.INTELLIGENCE,
                true,
                0,
                "2d8",
                "Slashing");

        SheetRequestDto sheetRequestDto = SheetRequestDto.builder()
                .name("Gamma")
                .campaignId(1L)
                .armorClass(10)
                .maxHp(28)
                .currentHp(23)
                .speed(6)
                .strength(12)
                .dexterity(14)
                .constitution(10)
                .intelligence(14)
                .wisdom(8)
                .charisma(13)
                .proficiencyBonus(2)
                .spellSlots(List.of(new SpellSlotRequestDto(1, 4, 3)))
                .attacks(List.of(attackRequestDto, attackRequestDto))
                .notes("char notes")
                .build();

        // Act
        SheetRequest sheetRequest = sheetMapper.fromDto(sheetRequestDto, 1L, 1L);

        // Assert
        assertNotNull(sheetRequest.spellSlots());
        assertEquals("char notes", sheetRequest.notes());
        assertEquals(2, sheetRequest.attacks().size());
        assertEquals("2d8", sheetRequest.attacks().getFirst().getDamageDie());
    }

    @Test
    void toAttackDtoList() {
        // Arrange
        Attack attack1 = new Attack("Dual blades",
                Ability.INTELLIGENCE,
                true,
                0,
                "2d8",
                "Slashing");

        Attack attack2 = new Attack("Quarterstaff",
                Ability.DEXTERITY,
                true,
                2,
                "2d6",
                "Magic");

        characterSheet.setAttacks(Set.of(attack1, attack2));

        // Act
        SheetDto sheetDto = sheetMapper.toDto(characterSheet);
        List<AttackDto> attackDtoList = sheetDto.attacks();

        AttackDto dualBlades = attackDtoList.stream()
                .filter(a -> a.name().equals(attack1.getName()))
                .findFirst().orElseThrow();

        // Assert
        assertEquals(2, attackDtoList.size());
        // The following fields are derived from the sheet
        assertEquals(6, dualBlades.attackBonus());
        assertEquals(3, dualBlades.damageModifier());
        assertEquals("2d8+3", characterSheet.getDamageFormula(attack1)); // Not yet used, covering for completion
    }

    @Test
    void toSpellDtoList() {
        // Arrange
        SpellSlot spellSlot1 = new SpellSlot(1, 4, 2);
        SpellSlot spellSlot2 = new SpellSlot(2, 3, 1);

        characterSheet.setSpellSlots(Set.of(spellSlot1, spellSlot2));

        // Act
        SheetDto sheetDto = sheetMapper.toDto(characterSheet);
        List<SpellSlotDto> spellSlotDtoList = sheetDto.spellSlots();

        SpellSlotDto spellSlotDto = spellSlotDtoList.stream()
                .filter(a -> a.level() == 1)
                .findFirst().orElseThrow();

        // Assert
        assertEquals(2, spellSlotDtoList.size());
        // Following field is derived from the sheet
        assertEquals(2, spellSlotDto.remaining());
    }

}