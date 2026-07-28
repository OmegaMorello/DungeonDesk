package com.marcomoretta.dungeondesk.domain.dto;

import com.marcomoretta.dungeondesk.domain.entity.Campaign;

import java.util.List;

public record AppUserDto(
        Long id,
        String name,
        List<Campaign> campaignList
) {
}
