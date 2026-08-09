package com.marcomoretta.dungeondesk.domain.entity;

/**
 * The eighteen skills of D&D 5e, each bound to the ability that governs it.
 * Keeping the association here means the sheet never has to hardcode which ability a
 * skill uses: the modifier is computed at runtime.
 */
public enum Skill {

    ATHLETICS(Ability.STRENGTH),

    ACROBATICS(Ability.DEXTERITY),
    SLEIGHT_OF_HAND(Ability.DEXTERITY),
    STEALTH(Ability.DEXTERITY),

    ARCANA(Ability.INTELLIGENCE),
    HISTORY(Ability.INTELLIGENCE),
    INVESTIGATION(Ability.INTELLIGENCE),
    NATURE(Ability.INTELLIGENCE),
    RELIGION(Ability.INTELLIGENCE),

    ANIMAL_HANDLING(Ability.WISDOM),
    INSIGHT(Ability.WISDOM),
    MEDICINE(Ability.WISDOM),
    PERCEPTION(Ability.WISDOM),
    SURVIVAL(Ability.WISDOM),

    DECEPTION(Ability.CHARISMA),
    INTIMIDATION(Ability.CHARISMA),
    PERFORMANCE(Ability.CHARISMA),
    PERSUASION(Ability.CHARISMA);

    private final Ability ability;

    Skill(Ability ability) {
        this.ability = ability;
    }

    public Ability getAbility() {
        return ability;
    }
}
