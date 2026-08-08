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

    /**
     * Resolves the join code a player typed into the running session.
     *
     * @param joinCode The code communicated by the Dungeon Master
     * @return The running session carrying that code, if any
     */
    @EntityGraph(attributePaths = {"campaign", "campaign.players"})
    Optional<GameSession> findByJoinCodeAndEndDateIsNull(String joinCode);

    /**
     * A session is running while its end date is null.
     * The application allows a single open session at a time; should two exist because
     * of a bug, this method fails.
     *
     * @return The running session, if any
     */
    @EntityGraph(attributePaths = {"campaign", "campaign.players"})
    Optional<GameSession> findByEndDateIsNull();

    @Override
    @EntityGraph(attributePaths = {"campaign"})
    Optional<GameSession> findById(Long sessionId);
}
