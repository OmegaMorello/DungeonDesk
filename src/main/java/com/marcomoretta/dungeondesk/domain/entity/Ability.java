package com.marcomoretta.dungeondesk.domain.entity;

/**
 * The six ability scores of D&D 5e. Every other number on a sheet derives from one of
 * them, so the enum is the key used by saving throws, skills and spellcasting.
 */
public enum Ability {
    STRENGTH,
    DEXTERITY,
    CONSTITUTION,
    INTELLIGENCE,
    WISDOM,
    CHARISMA
}
