package com.marcomoretta.dungeondesk.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Campaign: contains basic details, the ownerId,  a description, the players list
 * Offers methods to add or remove players
 */
@Entity
@Table(name = "campaign", uniqueConstraints = @UniqueConstraint(
        name = "uk_user_campaign",
        columnNames = {"campaign_id", "name"}))
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

    private String description;

    @ManyToOne(fetch = FetchType.LAZY) //
    @JoinColumn(name = "owner_id")
    @ToString.Exclude
    private AppUser owner;

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    private List<Player> players = new ArrayList<>();

    /**
     * Method to add a player to the campaign list
     *
     * @param player The player instance to add
     */
    public void addPlayer(Player player) {
        players.add(player);
        player.setCampaign(this);
    }

    /**
     * Method to remove a player from the campaign list
     *
     * @param player The player instance to remove
     */
    public void removePlayer(Player player) {
        players.remove(player);
        player.setCampaign(null);
    }
}
