package com.marcomoretta.dungeondesk.repository;

import com.marcomoretta.dungeondesk.domain.entity.Note;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Note persistency layer interface.
 */
@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    @Override
    @EntityGraph(attributePaths = {"campaign", "gameSession"})
    Optional<Note> findById(Long noteId);

    /**
     * All the notes of a campaign, session notes included. Dungeon Master view.
     *
     * @param campaignId The campaign to read
     * @return The notes in chronological order
     */
    @EntityGraph(attributePaths = {"campaign", "gameSession"})
    List<Note> findByCampaign_CampaignIdOrderByCreatedAsc(Long campaignId);

    /**
     * Only the notes of a campaign the players are allowed to read.
     *
     * @param campaignId The campaign to read
     * @return The shared notes in chronological order
     */
    @EntityGraph(attributePaths = {"campaign", "gameSession"})
    List<Note> findByCampaign_CampaignIdAndSharedWithPlayersTrueOrderByCreatedAsc(Long campaignId);

    /**
     * All the notes bound to a specific game session. Dungeon Master view.
     *
     * @param sessionId The session to read
     * @return The notes in chronological order
     */
    @EntityGraph(attributePaths = {"campaign", "gameSession"})
    List<Note> findByGameSession_SessionIdOrderByCreatedAsc(Long sessionId);

    /**
     * Only the notes of a game session the players are allowed to read.
     *
     * @param sessionId The session to read
     * @return The shared notes in chronological order
     */
    @EntityGraph(attributePaths = {"campaign", "gameSession"})
    List<Note> findByGameSession_SessionIdAndSharedWithPlayersTrueOrderByCreatedAsc(Long sessionId);
}
