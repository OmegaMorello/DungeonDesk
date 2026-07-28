package com.marcomoretta.dungeondesk.domain.dto;

import com.marcomoretta.dungeondesk.domain.entity.Campaign;

import java.util.List;

/**
 * Dto to expose not-sensitive fields of AppUser
 * @param id the id of the user
 * @param name the username of the user
 * @param campaignList the list of owned campaigns
 */
public record AppUserDto(
        Long id,
        String name,
        List<Campaign> campaignList
) {
}
