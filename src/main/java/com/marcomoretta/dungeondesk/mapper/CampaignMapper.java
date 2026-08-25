package com.marcomoretta.dungeondesk.mapper;

import com.marcomoretta.dungeondesk.domain.CampaignExport;
import com.marcomoretta.dungeondesk.domain.dto.CampaignDto;
import com.marcomoretta.dungeondesk.domain.dto.CampaignExportDto;
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
     *
     * @param dto Presentation layer request dto
     * @return The service layer request
     */
    CreateCampaignRequest fromCreateDto(CreateCampaignRequestDto dto, Long ownerId);

    /**
     * Maps a campaign update request from a dto
     *
     * @param dto Presentation layer request dto
     * @return The service layer request
     */
    UpdateCampaignRequest fromUpdateDto(UpdateCampaignRequestDto dto, Long campaignId);

    /**
     * Maps an add player request from a dto
     *
     * @param dto Presentation layer request dto
     * @return The service layer request
     */
    AddPlayerRequest fromAddPlayerDto(AddPlayerRequestDto dto, Long campaignId);

    /**
     * Maps a campaign to a dto
     *
     * @param campaign Service layer campaign
     * @return Presentation layer campaign dto
     */
    CampaignDto toDto(Campaign campaign);

    /**
     * Maps a campaign list to a dto
     *
     * @param campaignList Service layer campaign list
     * @return Presentation layer campaign list dto
     */
    List<CampaignDto> toDtoList(List<Campaign> campaignList);

    CampaignExportDto toExportDto(CampaignExport campaignExport);


    /**
     * Maps a player to a dto
     *
     * @param player Service layer player
     * @return Presentation layer player dto
     */
    PlayerDto toPlayerDto(Player player);

    /**
     * Maps a player list to a dto
     *
     * @param playerList Service layer player list
     * @return Presentation layer player dto list
     */
    List<PlayerDto> toPlayerDtoList(List<Player> playerList);
}
