package com.marcomoretta.dungeondesk.domain.dto;

import com.marcomoretta.dungeondesk.auth.LoginType;

/**
 * The session returned from a successful authentication
 *
 * @param loginType   The login type enum
 * @param displayName The name to be displayed at the client side
 * @param campaignId  The actual campaignId for the players only
 */
public record AuthSessionDto(
        LoginType loginType,
        String displayName,
        Long campaignId
) {
}
