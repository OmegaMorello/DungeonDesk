package com.marcomoretta.dungeondesk.repository;

import com.marcomoretta.dungeondesk.domain.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Player persistency layer interface
 * Player is not an aggregate root: this exists only to resolve a player by id when
 * assigning a character sheet, where no campaign is at hand
 */
@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
}
