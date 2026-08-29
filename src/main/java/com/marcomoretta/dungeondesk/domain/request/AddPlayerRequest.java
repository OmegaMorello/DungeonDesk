package com.marcomoretta.dungeondesk.domain.request;

/**
 * The request to add a player to an existing campaign in the service layer. To be used through a dto
 *
 * @param campaignId The id of the campaign to add the player in
 * @param name       The player name
 */
public record AddPlayerRequest(
        Long campaignId,
        String name
) {
}
