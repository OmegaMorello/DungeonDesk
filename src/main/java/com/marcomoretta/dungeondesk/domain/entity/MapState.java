package com.marcomoretta.dungeondesk.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;


/**
 * Map State: saves the actual state of the used map
 */
@Entity
@Table(name = "map_state")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MapState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long mapStateId;

    @Column(nullable = false)
    @Min(1)
    @Builder.Default
    private int gridRows = 1;

    @Column(nullable = false)
    @Min(1)
    @Builder.Default
    private int gridColumns = 1;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "campaign_id", nullable = false, unique = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    private Campaign campaign;

    @Builder.Default
    private String backgroundUrl = "";

    private String backgroundContentType;

    @OneToMany(mappedBy = "mapState", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    private List<Token> tokenList = new ArrayList<>();

    /**
     * Method to add a token to the map
     *
     * @param token The token instance to add
     */
    public void addToken(Token token) {
        tokenList.add(token);
        token.setMapState(this);
    }

    /**
     * Method to remove a token from the map
     *
     * @param token The token instance to remove
     */
    public void removeToken(Token token) {
        tokenList.remove(token);
        token.setMapState(null);
    }
}
