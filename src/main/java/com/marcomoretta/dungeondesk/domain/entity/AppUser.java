package com.marcomoretta.dungeondesk.domain.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Application User: usually the dungeon Master who owns 1 or more campaigns
 */
@Entity
@Table(name = "app_user")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(updatable = false, nullable = false)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String hashSecret;

}
