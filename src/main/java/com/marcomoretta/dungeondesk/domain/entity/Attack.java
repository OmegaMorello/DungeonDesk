package com.marcomoretta.dungeondesk.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

/**
 * A single attack or action available to a creature, player or enemy alike.
 */
@Embeddable
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Attack {

    @Column(nullable = false)
    private String name;


    // The ability this attack is based on
    @Enumerated(EnumType.STRING)
    private Ability ability;

    // Says if the creature is proficient with this weapon/attack
    private boolean proficient;

    // Bonus of a magic weapon/attack
    private int magicBonus;

    // Free text such as "1d8"
    private String damageDie;

    private String damageType;
}
