// The sheet request dto, including derived values
export function toSheetRequest(sheet, campaignId) {
    return {
        campaignId,
        name: sheet.name,
        armorClass: sheet.armorClass,
        maxHp: sheet.maxHp,
        currentHp: sheet.currentHp,
        speed: sheet.speed,
        strength: sheet.strength,
        dexterity: sheet.dexterity,
        constitution: sheet.constitution,
        intelligence: sheet.intelligence,
        wisdom: sheet.wisdom,
        charisma: sheet.charisma,
        proficiencyBonus: sheet.proficiencyBonus,
        skillProficiencies: sheet.skillProficiencies,
        skillExpertise: sheet.skillExpertise,
        savingThrowProficiencies: sheet.savingThrowProficiencies,
        spellcastingAbility: sheet.spellcastingAbility,
        spellSlots: sheet.spellSlots,
        attacks: sheet.attacks,
        notes: sheet.notes,
        // CHARACTER only
        playerId: sheet.playerId,
        level: sheet.level,
        characterClass: sheet.characterClass,
        species: sheet.species,
        experiencePoints: sheet.experiencePoints,
        hitDiceSize: sheet.hitDiceSize,
        hitDiceRemaining: sheet.hitDiceRemaining,
        deathSaveSuccesses: sheet.deathSaveSuccesses,
        deathSaveFailures: sheet.deathSaveFailures,
        exhaustion: sheet.exhaustion,
        inspiration: sheet.inspiration,
        // ENEMY only
        challengeRating: sheet.challengeRating,
        creatureType: sheet.creatureType,
        experienceReward: sheet.experienceReward,
    };
}

// Pre-compiled template to create new sheets
export function emptySheet(sheetType) {
    return {
        sheetType,
        name: "",
        armorClass: 10,
        maxHp: 1,
        currentHp: 1,
        speed: 30,
        strength: 10,
        dexterity: 10,
        constitution: 10,
        intelligence: 10,
        wisdom: 10,
        charisma: 10,
        proficiencyBonus: 2,
        skillProficiencies: [],
        skillExpertise: [],
        savingThrowProficiencies: [],
        spellSlots: [],
        attacks: [],
        notes: "",
        ...(sheetType === "CHARACTER"
            ? {
                level: 1,
                characterClass: "",
                species: "",
                experiencePoints: 0,
                hitDiceSize: 8,
                hitDiceRemaining: 1,
                deathSaveSuccesses: 0,
                deathSaveFailures: 0,
                exhaustion: 0,
                inspiration: false
            }
            : {
                challengeRating: "",
                creatureType: "",
                experienceReward: 0
            }),
    };
}