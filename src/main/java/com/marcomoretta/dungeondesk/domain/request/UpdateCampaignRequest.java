package com.marcomoretta.dungeondesk.domain.request;

/**
 * The request to update an existing campaign in the service layer. To be used through a dto
 *
 * @param campaignId  The id of the campaign to update
 * @param name        The updated name
 * @param description The updated description
 */
public record UpdateCampaignRequest(
        Long campaignId,
        String name,
        String description
) {
}
