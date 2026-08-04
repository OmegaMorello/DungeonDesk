package com.marcomoretta.dungeondesk.domain.request;

/**
 * The request to create a new campaign in the service layer. To be used through a dto
 *
 * @param name        The campaign name
 * @param ownerId     The app user id of the ownerId
 * @param description A description of the campaign
 */
public record CreateCampaignRequest(
        String name,
        Long ownerId,
        String description
) {
}
