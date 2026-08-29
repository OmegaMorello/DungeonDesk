package com.marcomoretta.dungeondesk.domain.dto;

import java.util.List;

/**
 * Dto to expose fields of Campaign
 *
 * @param campaignId          The id of the campaign
 * @param name        The name of the campaign
 * @param description The description of the campaign
 * @param ownerId     The campaign owner id
 * @param ownerName   The campaign owner name
 * @param players     The list of players
 */
public record CampaignDto(
        Long campaignId,
        String name,
        String description,
        Long ownerId,
        String ownerName,
        List<PlayerDto> players
) {
}
