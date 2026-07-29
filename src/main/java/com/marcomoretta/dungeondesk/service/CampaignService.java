package com.marcomoretta.dungeondesk.service;

import com.marcomoretta.dungeondesk.domain.entity.Campaign;
import com.marcomoretta.dungeondesk.domain.request.AddPlayerRequest;
import com.marcomoretta.dungeondesk.domain.request.CreateCampaignRequest;
import com.marcomoretta.dungeondesk.domain.request.UpdateCampaignRequest;

import java.util.List;

/**
 * Interface that defines the Campaign Service
 */
public interface CampaignService {
    /**
     * Creates a new campaign
     * @param request The request with the body to create a new campaign
     * @return The newly created Campaign or an error which will be raised by the controller
     */
    Campaign createCampaign(CreateCampaignRequest request);

    /**
     * Gets the list of all campaigns
     * @return List containing all the campaign information
     */
    List<Campaign> getAllCampaigns();

    //TODO: user needs to be authenticated to delete a campaign
    /**
     * Deletes a campaign
     * @param id The id of the campaign to delete
     * @return The deleted campaign
     */
    Campaign deleteCampaign(Long id);

    /**
     * Exports the campaign setting the ownerId to null
     * @return Exported Campaign
     */
    Campaign exportCampaign(Long id);

    /**
     * Updates campaign information such as name and secret
     * @param request Information to update
     * @return Updated Campaign
     */
    Campaign updateCampaign(UpdateCampaignRequest request);

    /**
     * Adds a Player to a Campaign players list
     * @param request The request to add a player
     * @return The campaign with the updated players list
     */
    Campaign addPlayer(AddPlayerRequest request);

    /**
     * Removes a player from the campaign players list
     * @param campaignId The id of the campaign
     * @param playerId The id of the player to be removed
     * @return The campaign with the updated players list
     */
    Campaign removePlayer(Long campaignId, Long playerId);
}
