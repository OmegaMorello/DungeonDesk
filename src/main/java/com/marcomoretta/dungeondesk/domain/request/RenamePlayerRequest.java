package com.marcomoretta.dungeondesk.domain.request;

/**
 * The request to rename a player. To be used through a dto
 *
 * @param campaignId The id of the campaign the player belongs to
 * @param playerId   The id of the player to rename
 * @param name       The player name
 */
public record RenamePlayerRequest(
        Long campaignId,
        Long playerId,
        String name
) {
}
