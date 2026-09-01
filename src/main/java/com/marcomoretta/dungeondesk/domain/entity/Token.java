package com.marcomoretta.dungeondesk.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * The pawn associated to a CharSheet
 * Can be a Playing Character [PC] or Non-Playing Character [NPC]
 */
@Entity
@Table(name = "token")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    private Long tokenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "map_state_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    private MapState mapState;

    @Column(nullable = false)
    @Min(1)
    private int posX;

    @Column(nullable = false)
    @Min(1)
    private int posY;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TokenType type;


    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sheet_id", nullable = false, unique = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    private GenericSheet sheet;
}
