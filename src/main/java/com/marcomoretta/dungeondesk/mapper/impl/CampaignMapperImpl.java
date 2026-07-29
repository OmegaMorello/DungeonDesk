package com.marcomoretta.dungeondesk.mapper.impl;

import com.marcomoretta.dungeondesk.domain.dto.CampaignDto;
import com.marcomoretta.dungeondesk.domain.dto.request.AddPlayerRequestDto;
import com.marcomoretta.dungeondesk.domain.dto.request.CreateCampaignRequestDto;
import com.marcomoretta.dungeondesk.domain.dto.request.UpdateCampaignRequestDto;
import com.marcomoretta.dungeondesk.domain.entity.Campaign;
import com.marcomoretta.dungeondesk.domain.request.AddPlayerRequest;
import com.marcomoretta.dungeondesk.domain.request.CreateCampaignRequest;
import com.marcomoretta.dungeondesk.domain.request.UpdateCampaignRequest;
import com.marcomoretta.dungeondesk.mapper.CampaignMapper;
import org.springframework.stereotype.Component;

import java.util.List;

//TODO: Implement Campaign Mapper
@Component
public class CampaignMapperImpl implements CampaignMapper {
    @Override
    public CreateCampaignRequest fromCreateDto(CreateCampaignRequestDto dto) {
        return null;
    }

    @Override
    public UpdateCampaignRequest fromUpdateDto(UpdateCampaignRequestDto dto, Long campaignId) {
        return null;
    }

    @Override
    public AddPlayerRequest fromAddPlayerDto(AddPlayerRequestDto dto, Long campaignId) {
        return null;
    }

    @Override
    public CampaignDto toDto(Campaign campaign) {
        return null;
    }

    @Override
    public List<CampaignDto> toDtoList(List<Campaign> campaignList) {
        return List.of();
    }
}
