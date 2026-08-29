package com.marcomoretta.dungeondesk.domain.dto;

import java.time.Instant;

/**
 * Dto to expose fields of GameSession.
 * Only returned to the Dungeon Master: it carries the join code, which is the
 * credential players use to connect and must never reach an unauthenticated caller.
 *
 * @param sessionId  The id of the session
 * @param campaignId The campaign the session belongs to
 * @param joinCode   The code the Dungeon Master communicates to the players
 * @param startDate  When the session was opened
 * @param endDate    When the session was closed, null while it is running
 */
public record GameSessionDto(
        Long sessionId,
        Long campaignId,
        String campaignName,
        String joinCode,
        Instant startDate,
        Instant endDate
) {
}
