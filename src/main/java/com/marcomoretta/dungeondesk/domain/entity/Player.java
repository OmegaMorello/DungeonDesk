package com.marcomoretta.dungeondesk.domain.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Player: the user who connects to the session hosted by the Dungeon Master
 * Has a unique constraint on the campaign id
 */
@Entity
@Table(name = "player", uniqueConstraints = @UniqueConstraint(
        name = "uk_player_campaign",
        columnNames = {"campaign_id", "name"}
))
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(updatable = false, nullable = false)
    private Long playerId;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    @ToString.Exclude
    private Campaign campaign;

    //TODO: implement CharSheet class
//    @ManyToMany
//    @JoinColumn(name = "char_sheet_id")
//    @ToString.Exclude
//    @Builder.Default
//    private List<CharSheet> charSheets = new ArrayList<>()
}
