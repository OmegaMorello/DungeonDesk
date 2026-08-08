package com.marcomoretta.dungeondesk.auth;

import com.marcomoretta.dungeondesk.exception.ResourcePermissionException;
import lombok.Builder;

import java.time.Instant;

/**
 * Custom authentication session container
 *
 * @param token       Unique identifier sent to the client and returned on every request
 * @param loginType   Determines which fields are populated
 * @param userId      MASTER only userId
 * @param playerId    PLAYER only playerId
 * @param campaignId  PLAYER only campaignId
 * @param displayName The name to be displayed
 * @param createdAt   creation timestamp
 */
@Builder(toBuilder = true) // Enables continue building between methods - see SessionStore
public record AuthSession(
        String token,
        LoginType loginType,
        Long userId,
        Long playerId,
        Long campaignId,
        String displayName,
        Instant createdAt
) {
    /**
     * Avoids a Player to potentially ask for a Master only resource
     */
    public void requireMaster() {
        if (!loginType.equals(LoginType.MASTER))
            throw new ResourcePermissionException("Only a Dungeon Master can perform this operation");
    }
}
