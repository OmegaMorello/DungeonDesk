package com.marcomoretta.dungeondesk.repository;

import com.marcomoretta.dungeondesk.domain.entity.GameSession;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Game Session persistency layer interface
 * EntityGraphs are used to extract lazy fetch fields
 */
@Repository
public interface GameSessionRepository extends JpaRepository<GameSession, Long> {

    @EntityGraph(attributePaths = {"campaign", "campaign.players"})
    Optional<GameSession> findByJoinCodeAndEndDateIsNull(String joinCode);
}
