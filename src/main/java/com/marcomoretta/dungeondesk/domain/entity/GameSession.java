package com.marcomoretta.dungeondesk.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

/**
 * Game Session: the current session, only one per campaign
 */
@Entity
@Table(name = "game_session")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class GameSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(updatable = false, nullable = false)
    private Long sessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    private Campaign campaign;

    /**
     * Session join code will not be hashed, it is used by players to connect to a session.
     * Dungeon Master can change it anytime.
     */
    @Column(nullable = false)
    private String joinCode;

    @CreationTimestamp
    private Instant startDate;

    private Instant endDate;
}
