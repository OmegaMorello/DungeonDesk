package com.marcomoretta.dungeondesk.auth;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory container of the current authentication sessions
 */
@Component
public class SessionStore {
    // ConcurrentHashMap provides lock-free readings and per-bucket writing locks
    // Map is shared by multiple threads, one for each http request
    private final Map<String, AuthSession> sessions = new ConcurrentHashMap<>();

    /**
     * Save the session in the Hash Map with the randomly generated token as the key
     *
     * @param session The auth session to be saved
     * @return The saved auth session
     */
    private AuthSession store(AuthSession session) {
        String token = UUID.randomUUID().toString();

        AuthSession stored = session.toBuilder()
                .token(token)
                .createdAt(Instant.now())
                .build();

        sessions.put(stored.token(), stored);
        return stored;
    }


    /**
     * Create the Master session, passing the building parameters to the store method
     *
     * @param userId      The requester user id
     * @param displayName The name to be displayed
     * @return An unfinished auth session, to be completed by the store method
     */
    public AuthSession createMasterSession(Long userId, String displayName) {

        return store(AuthSession.builder()
                .loginType(LoginType.MASTER)
                .userId(userId)
                .displayName(displayName)
                .build());
    }

    /**
     * Create the Player session, passing the building parameters to the store method
     *
     * @param playerId    The requester player id
     * @param campaignId  The id of the campaign
     * @param sessionId   The id of the session
     * @param displayName The name to be displayed
     * @return An unfinished auth session, to be completed by the store method
     */
    public AuthSession createPlayerSession(Long playerId, Long campaignId, Long sessionId, String displayName) {

        return store(AuthSession.builder()
                .loginType(LoginType.PLAYER)
                .playerId(playerId)
                .campaignId(campaignId)
                .sessionId(sessionId)
                .displayName(displayName)
                .build());

    }

    /**
     * Search for a session with a specified token
     * Used for a persistent authentication of the client
     *
     * @param token The UUID sent by the client
     * @return The session in case it was found
     */
    public Optional<AuthSession> find(String token) {
        return Optional.ofNullable(sessions.get(token));
    }

    /**
     * Removes the session with the specified
     * Used for the logout of the client
     *
     * @param token The token of the session to remove
     */
    public void remove(String token) {
        sessions.remove(token);
    }

}
