package com.marcomoretta.dungeondesk.repository;

import com.marcomoretta.dungeondesk.domain.entity.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Game Session persistency layer interface
 */
@Repository
public interface GameSessionRepository extends JpaRepository<GameSession, Long> {
}
