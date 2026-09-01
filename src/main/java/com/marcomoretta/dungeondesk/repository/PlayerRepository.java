package com.marcomoretta.dungeondesk.repository;

import com.marcomoretta.dungeondesk.domain.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Player persistency layer interface
 */
@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
}
