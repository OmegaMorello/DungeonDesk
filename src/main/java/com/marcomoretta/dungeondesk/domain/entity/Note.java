package com.marcomoretta.dungeondesk.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

/**
 * Note: free notes to be taken during a campaign
 */
@Entity
@Table(name = "note")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    private Long noteId;

    /**
     * Notes must be associated to a campaign
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "campaign_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    private Campaign campaign;

    /**
     * Optionally, notes can be assigned to a specific game session
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_session_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @ToString.Exclude
    private GameSession gameSession;

    /**
     * False: visible only to DM
     * True: visible to DM + all players
     */
    @Column(nullable = false)
    private boolean sharedWithPlayers;

    @Column(nullable = false, length = 4000)
    private String text;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant created;
}
