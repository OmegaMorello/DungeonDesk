package com.marcomoretta.dungeondesk.mapper.impl;

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
import com.marcomoretta.dungeondesk.mapper.CampaignMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CampaignMapperImpl implements CampaignMapper {
    @Override
    public CreateCampaignRequest fromCreateDto(CreateCampaignRequestDto dto) {
        return new CreateCampaignRequest(
                dto.name(),
                dto.ownerId(),
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
