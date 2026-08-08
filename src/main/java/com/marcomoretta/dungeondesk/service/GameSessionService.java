package com.marcomoretta.dungeondesk.service;

import com.marcomoretta.dungeondesk.domain.entity.GameSession;
import com.marcomoretta.dungeondesk.domain.entity.Player;
import com.marcomoretta.dungeondesk.domain.request.CreateGameSessionRequest;
import com.marcomoretta.dungeondesk.domain.request.UpdateGameSessionRequest;

import java.util.List;
import java.util.Optional;

/**
 * Interface that defines the GameSession Service.
 * The application allows a single open session at a time.
 */
public interface GameSessionService {

    /**
     * Gets the session currently running, if there is one.
     *
     * @return The running session, if any
     */
    Optional<GameSession> getActiveSession();

    /**
     * Gets the roster of the campaign whose session is running.
     * Used by the public login screen to let a player pick their name.
     *
     * @return The players of the running session, empty when no session is open
     */
    List<Player> getActiveSessionRoster();

    /**
     * Opens a new session on a campaign
     *
     * @param request     The session creation request details
     * @param requesterId The id of the user asking, must own the campaign
     * @return The newly opened session
     */
    GameSession createNewSession(CreateGameSessionRequest request, Long requesterId);

    /**
     * Closes a running session by setting its end date
     *
     * @param sessionId   The id of the session to close
     * @param requesterId The id of the user asking, must own the campaign
     * @return The closed session
     */
    GameSession closeSession(Long sessionId, Long requesterId);

    /**
     * Changes the join code of a session, which the Dungeon Master may do at any time
     *
     * @param request     The session update request details
     * @param requesterId The id of the user asking, must own the campaign
     * @return The updated session
     */
    GameSession updateSession(UpdateGameSessionRequest request, Long requesterId);
}
