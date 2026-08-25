package com.marcomoretta.dungeondesk.service;

import com.marcomoretta.dungeondesk.domain.CampaignExport;
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
     *
     * @param request The request with the body to create a new campaign
     * @return The newly created Campaign or an error which will be raised by the controller
     */
    Campaign createCampaign(CreateCampaignRequest request);

    /**
     * Gets a campaign by its id
     *
     * @param campaignId The id of the campaign to retrieve
     * @return The complete Campaign object
     */
    Campaign getCampaign(Long campaignId);

    /**
     * Gets the list of all campaigns owned by the user
     *
     * @param ownerId The logged in user id
     * @return List containing all the campaign information
     */
    List<Campaign> getAllCampaigns(Long ownerId);

    /**
     * Deletes a campaign
     *
     * @param campaignId  The id of the campaign to delete
     * @param requesterId The id of the requester
     */
    void deleteCampaign(Long campaignId, Long requesterId);

    /**
     * Exports the campaign setting the ownerId to null
     *
     * @return Exported Campaign
     */
    CampaignExport exportCampaign(Long campaignId, Long requesterId);

    /**
     * Updates campaign information such as name and secret
     *
     * @param request     Information to update
     * @param requesterId The id of the requester
     * @return Updated Campaign
     */
    Campaign updateCampaign(UpdateCampaignRequest request, Long requesterId);

    /**
     * Adds a Player to a Campaign players list
     *
     * @param request The request to add a player
     * @return The campaign with the updated players list
     */
    Campaign addPlayer(AddPlayerRequest request, Long requesterId);

    /**
     * Removes a player from the campaign players list
     *
     * @param campaignId The id of the campaign
     * @param playerId   The id of the player to be removed
     * @return The campaign with the updated players list
     */
    Campaign removePlayer(Long campaignId, Long playerId, Long requesterId);
}
