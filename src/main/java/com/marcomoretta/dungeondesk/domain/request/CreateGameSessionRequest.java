package com.marcomoretta.dungeondesk.domain.request;

/**
 * The request to open a new game session in the service layer. To be used through a dto.
 *
 * @param campaignId The campaign the session belongs to
 * @param joinCode   The code chosen by the Dungeon Master
 */
public record CreateGameSessionRequest(
        Long campaignId,
        String joinCode
) {
}
