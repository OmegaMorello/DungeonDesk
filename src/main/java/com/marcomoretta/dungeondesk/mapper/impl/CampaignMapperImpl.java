package com.marcomoretta.dungeondesk.mapper.impl;

import com.marcomoretta.dungeondesk.domain.CampaignExport;
import com.marcomoretta.dungeondesk.domain.dto.CampaignDto;
import com.marcomoretta.dungeondesk.domain.dto.CampaignExportDto;
import com.marcomoretta.dungeondesk.domain.dto.PlayerDto;
import com.marcomoretta.dungeondesk.domain.dto.request.AddPlayerRequestDto;
import com.marcomoretta.dungeondesk.domain.dto.request.CreateCampaignRequestDto;
import com.marcomoretta.dungeondesk.domain.dto.request.RenamePlayerRequestDto;
import com.marcomoretta.dungeondesk.domain.dto.request.UpdateCampaignRequestDto;
import com.marcomoretta.dungeondesk.domain.entity.Campaign;
import com.marcomoretta.dungeondesk.domain.entity.Player;
import com.marcomoretta.dungeondesk.domain.request.AddPlayerRequest;
import com.marcomoretta.dungeondesk.domain.request.CreateCampaignRequest;
import com.marcomoretta.dungeondesk.domain.request.RenamePlayerRequest;
import com.marcomoretta.dungeondesk.domain.request.UpdateCampaignRequest;
import com.marcomoretta.dungeondesk.mapper.CampaignMapper;
import com.marcomoretta.dungeondesk.mapper.GameSessionMapper;
import com.marcomoretta.dungeondesk.mapper.NoteMapper;
import com.marcomoretta.dungeondesk.mapper.SheetMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper for campaign DTOs
 */
@Component
public class CampaignMapperImpl implements CampaignMapper {
    private final NoteMapper noteMapper;
    private final GameSessionMapper gameSessionMapper;
    private final SheetMapper sheetMapper;

    public CampaignMapperImpl(NoteMapper noteMapper, GameSessionMapper gameSessionMapper, SheetMapper sheetMapper) {
        this.noteMapper = noteMapper;
        this.gameSessionMapper = gameSessionMapper;
        this.sheetMapper = sheetMapper;
    }

    @Override
    public CreateCampaignRequest fromCreateDto(CreateCampaignRequestDto dto, Long ownerId) {
        return new CreateCampaignRequest(
                dto.name(),
                ownerId,
                dto.description()
        );
    }

    @Override
    public UpdateCampaignRequest fromUpdateDto(UpdateCampaignRequestDto dto, Long campaignId) {
        return new UpdateCampaignRequest(
                campaignId,
                dto.name(),
                dto.description()
        );
    }

    @Override
    public AddPlayerRequest fromAddPlayerDto(AddPlayerRequestDto dto, Long campaignId) {
        return new AddPlayerRequest(
                campaignId,
                dto.name()
        );
    }

    @Override
    public RenamePlayerRequest fromRenamePlayerDto(RenamePlayerRequestDto dto, Long campaignId, Long playerId) {
        return new RenamePlayerRequest(
                campaignId,
                playerId,
                dto.name()
        );
    }

    @Override
    public CampaignDto toDto(Campaign campaign) {
        return new CampaignDto(
                campaign.getCampaignId(),
                campaign.getName(),
                campaign.getDescription(),
                campaign.getOwner().getUserId(),
                campaign.getOwner().getUsername(),
                toPlayerDtoList(campaign.getPlayers())
        );
    }

    @Override
    public List<CampaignDto> toDtoList(List<Campaign> campaignList) {
        return campaignList.stream().map(this::toDto).toList();
    }

    @Override
    public CampaignExportDto toExportDto(CampaignExport campaignExport) {
        return new CampaignExportDto(
                toDto(campaignExport.campaign()),
                noteMapper.toDtoList(campaignExport.notes()),
                gameSessionMapper.toDtoList(campaignExport.gameSessions()),
                toPlayerDtoList(campaignExport.campaign().getPlayers()),
                sheetMapper.toDtoList(campaignExport.sheets())
        );
    }

    @Override
    public PlayerDto toPlayerDto(Player player) {
        return new PlayerDto(
                player.getPlayerId(),
                player.getName()
        );
    }

    @Override
    public List<PlayerDto> toPlayerDtoList(List<Player> playerList) {
        return playerList.stream().map(this::toPlayerDto).toList();
    }
}
