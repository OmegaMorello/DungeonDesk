package com.marcomoretta.dungeondesk.domain.dto;

import com.marcomoretta.dungeondesk.auth.LoginType;

/**
 * Dto that stores the session info
 *
 * @param loginType The type of login
 * @param displayName The name to display
 * @param campaignId The campaign id - PLAYERS only
 */
public record SessionInfoDto(
        LoginType loginType,
        String displayName,
        Long campaignId
) {
}
