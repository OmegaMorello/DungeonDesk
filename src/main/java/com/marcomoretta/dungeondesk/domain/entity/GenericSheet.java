package com.marcomoretta.dungeondesk.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Common part of every creature sheet, player characters and enemies.
 */
@Entity
@Table(name = "sheet")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "sheet_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@ToString
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class GenericSheet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(updatable = false, nullable = false)
    private Long sheetId;

    /**
     * The Dungeon Master who created the sheet.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    @ToString.Exclude
    private AppUser owner;

    @Column(nullable = false)
    private String name;

    // ---- Defences and hit points -------------------------------------------------

    private int armorClass;
    private int maxHp;
    private int currentHp;
    private int speed;

    // ---- Ability scores ----------------------------------------------------------

    private int strength;
    private int dexterity;
    private int constitution;
    private int intelligence;
    private int wisdom;
    private int charisma;


    // ---- Proficiencies -----------------------------------------------------------

    private int proficiencyBonus;

    /**
     * Skills the creature is proficient with: the proficiency bonus is added once to
     * their modifier.
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "sheet_skill_proficiency", joinColumns = @JoinColumn(name = "sheet_id"))
    @Column(name = "skill")
    @Enumerated(EnumType.STRING)
    @ToString.Exclude
    @Builder.Default
    private Set<Skill> skillProficiencies = EnumSet.noneOf(Skill.class);

    /**
     * Skills with expertise: the proficiency bonus is added a second time. A skill
     * listed here is expected to be listed among the proficiencies as well.
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "sheet_skill_expertise", joinColumns = @JoinColumn(name = "sheet_id"))
    @Column(name = "skill")
    @Enumerated(EnumType.STRING)
    @ToString.Exclude
    @Builder.Default
    private Set<Skill> skillExpertise = EnumSet.noneOf(Skill.class);

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "sheet_saving_throw", joinColumns = @JoinColumn(name = "sheet_id"))
    @Column(name = "ability")
    @Enumerated(EnumType.STRING)
    @ToString.Exclude
    @Builder.Default
    private Set<Ability> savingThrowProficiencies = EnumSet.noneOf(Ability.class);

    // ---- Spellcasting ------------------------------------------------------------

    /**
     * Ability used to cast spells, null for a creature that does not cast.
     */
    @Enumerated(EnumType.STRING)
    private Ability spellcastingAbility;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "sheet_spell_slot", joinColumns = @JoinColumn(name = "sheet_id"))
    @ToString.Exclude
    @Builder.Default
    private List<SpellSlot> spellSlots = new ArrayList<>();

    // ---- Actions -----------------------------------------------------------------

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "sheet_attack", joinColumns = @JoinColumn(name = "sheet_id"))
    @ToString.Exclude
    @Builder.Default
    private List<Attack> attacks = new ArrayList<>();

    /**
     * Everything the sheet does not model as a number: equipment, traits, languages, senses, resistances.
     * Kept as free text on purpose so that players and DM can add info as they please
     */
    @Column(length = 4000)
    private String notes;

    // ==== Derived values ==========================================================

    /**
     * Reads one of the six scores through the enum
     *
     * @param ability The ability to read
     * @return Its raw score
     */
    public int getAbilityScore(Ability ability) {
        return switch (ability) {
            case STRENGTH -> strength;
            case DEXTERITY -> dexterity;
            case CONSTITUTION -> constitution;
            case INTELLIGENCE -> intelligence;
            case WISDOM -> wisdom;
            case CHARISMA -> charisma;
        };
    }

    /**
     * Ability modifier as defined by the rules: the score minus ten, halved and rounded down
     *
     * @param ability The ability to evaluate
     * @return Its modifier, negative for scores below ten
     */
    public int getAbilityModifier(Ability ability) {
        return Math.floorDiv(getAbilityScore(ability) - 10, 2);
    }

    /**
     * Saving throw modifier: the ability modifier, plus the proficiency bonus when the
     * creature is proficient with that save
     *
     * @param ability The saving throw ability to evaluate
     * @return The total modifier to roll with
     */
    public int getSavingThrow(Ability ability) {
        int bonus = savingThrowProficiencies.contains(ability) ? proficiencyBonus : 0;

        return getAbilityModifier(ability) + bonus;
    }

    /**
     * Skill modifier: the modifier of the governing ability, plus the proficiency bonus
     * once if proficient and a second time if the creature has expertise
     *
     * @param skill The skill to evaluate
     * @return The total modifier to roll with
     */
    public int getSkillModifier(Skill skill) {
        int bonus = 0;

        if (skillProficiencies.contains(skill)) bonus += proficiencyBonus;
        if (skillExpertise.contains(skill)) bonus += proficiencyBonus;

        return getAbilityModifier(skill.getAbility()) + bonus;
    }

    /**
     * Initiative: the Dexterity modifier
     *
     * @return The total modifier to roll with
     */
    public int getInitiative() {
        return getAbilityModifier(Ability.DEXTERITY);
    }

    /**
     * Passive Perception: ten plus the Perception modifier.
     *
     * @return The passive perception score
     */
    public int getPassivePerception() {
        return 10 + getSkillModifier(Skill.PERCEPTION);
    }

    /**
     * Difficulty class of the spells cast by this creature: eight plus the proficiency
     * bonus plus the spellcasting ability modifier.
     *
     * @return The save DC, or null when the creature does not cast spells
     */
    public Integer getSpellSaveDc() {
        if (spellcastingAbility == null) return null;

        return 8 + proficiencyBonus + getAbilityModifier(spellcastingAbility);
    }

    /**
     * Attack bonus for spell attacks: the proficiency bonus plus the spellcasting
     * ability modifier.
     *
     * @return The bonus, or null when the creature does not cast spells
     */
    public Integer getSpellAttackBonus() {
        if (spellcastingAbility == null) return null;

        return proficiencyBonus + getAbilityModifier(spellcastingAbility);
    }

    /**
     * The attack bonus calculated on modifier, proficiency and magic bonus (if any).
     * Used to see if the creature can hit
     *
     * @param attack The attack/weapon on which the bonus has to be calculated
     * @return The int bonus to be added to the D20 roll
     */
    public int getAttackBonus(Attack attack) {
        int abilityModifier = attack.getAbility() == null ? 0 : getAbilityModifier(attack.getAbility());
        int proficiency = attack.isProficient() ? proficiencyBonus : 0;

        return abilityModifier + proficiency + attack.getMagicBonus();
    }

    /**
     * The damage modifier to be applied when an attack hits.
     * Only considers the ability modifier
     *
     * @param attack The attack/weapon to evaluate
     * @return The int modifier to add to the damage roll
     */
    public int getDamageModifier(Attack attack) {
        return attack.getAbility() == null ? 0 : getAbilityModifier(attack.getAbility());
    }

}
