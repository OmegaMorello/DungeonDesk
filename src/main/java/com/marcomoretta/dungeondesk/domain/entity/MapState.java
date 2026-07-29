package com.marcomoretta.dungeondesk.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;


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
    private Long mapId;

    @Column(nullable = false)
    @Min(1)
    private int gridRows = 1;

    @Column(nullable = false)
    @Min(1)
    @Builder.Default
    private int gridColumns = 1;

    @Builder.Default
    private String backgroundUrl = "";

    //TODO: implement CharToken
//    @OneToMany(mappedBy = "char_token")
//    @ToString.Exclude
//    @Builder.Default
//    private List<CharToken> charTokenList = new ArrayList<>();
}
