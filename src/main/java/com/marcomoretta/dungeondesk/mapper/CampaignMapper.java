package com.marcomoretta.dungeondesk.mapper;

import com.marcomoretta.dungeondesk.domain.dto.CampaignDto;
import com.marcomoretta.dungeondesk.domain.dto.PlayerDto;
import com.marcomoretta.dungeondesk.domain.dto.request.AddPlayerRequestDto;
import com.marcomoretta.dungeondesk.domain.dto.request.CreateCampaignRequestDto;
import com.marcomoretta.dungeondesk.domain.dto.request.UpdateCampaignRequestDto;
import com.marcomoretta.dungeondesk.domain.entity.Campaign;
import com.marcomoretta.dungeondesk.domain.entity.Player;
import com.marcomoretta.dungeondesk.domain.request.AddPlayerRequest;
import com.marcomoretta.dungeondesk.domain.request.CreateCampaignRequest;
import com.marcomoretta.dungeondesk.domain.request.UpdateCampaignRequest;

import java.util.List;

/**
 * Campaign DTO mapper interface
 */
public interface CampaignMapper {

    /**
     * Maps a campaign create request from a dto
     * @param dto presentation layer request dto
     * @return the service layer request
     */
    CreateCampaignRequest fromCreateDto(CreateCampaignRequestDto dto);

    /**
     * Maps a campaign update request from a dto
     * @param dto presentation layer request dto
     * @return the service layer request
     */
    UpdateCampaignRequest fromUpdateDto(UpdateCampaignRequestDto dto, Long campaignId);

    /**
     * Maps an add player request from a dto
     * @param dto presentation layer request dto
     * @return the service layer request
     */
    AddPlayerRequest fromAddPlayerDto(AddPlayerRequestDto dto, Long campaignId);

    /**
     * Maps a campaign to a dto
     * @param campaign service layer campaign
     * @return presentation layer campaign dto
     */
    CampaignDto toDto(Campaign campaign);

    /**
     * Maps a campaign list to a dto
     * @param campaignList service layer campaign list
     * @return presentation layer campaign list dto
     */
    List<CampaignDto> toDtoList(List<Campaign> campaignList);

    /**
     * Maps a player to a dto
     * @param player service layer player
     * @return presentation layer player dto
     */
    PlayerDto toPlayerDto(Player player);

    /**
     * Maps a player list to a dto
     * @param playerList service layer player list
     * @return presentation layer player dto list
     */
    List<PlayerDto> toPlayerDtoList(List<Player> playerList);
}
