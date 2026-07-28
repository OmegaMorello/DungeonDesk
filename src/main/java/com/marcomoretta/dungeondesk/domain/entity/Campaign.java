package com.marcomoretta.dungeondesk.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.AnyDiscriminatorImplicitValues;

/**
 * Campaign: contains basic details, the owner and a description
 */
@Entity
@Table(name = "campaign")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(updatable = false, nullable = false)
    private Long campaignId;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY) //
    @JoinColumn(name = "owner_id")
    @ToString.Exclude
    private AppUser owner;

    /**
     * Session code will not be hashed, it is used by players to connect to a session.
     * Dungeon Master can change it anytime.
     */
    @Column(nullable = false)
    private String sessionCode;

    private String description;
}
