package com.marcomoretta.dungeondesk.domain.dto;

import com.marcomoretta.dungeondesk.auth.LoginType;

/**
 * The session returned from a successful authentication
 *
 * @param loginType   The login type enum
 * @param displayName The name to be displayed at the client side
 * @param campaignId  The actual campaign id for the players only
 * @param sessionId   The actual session id for the players only
 * @param playerId    The player id
 */
public record AuthSessionDto(
        LoginType loginType,
        String displayName,
        Long campaignId,
        Long sessionId,
        Long playerId
) {
}
