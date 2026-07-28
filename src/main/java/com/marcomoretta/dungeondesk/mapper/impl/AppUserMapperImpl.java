package com.marcomoretta.dungeondesk.mapper.impl;

import com.marcomoretta.dungeondesk.mapper.AppUserMapper;
import com.marcomoretta.dungeondesk.domain.request.CreateAppUserRequest;
import com.marcomoretta.dungeondesk.domain.dto.AppUserDto;
import com.marcomoretta.dungeondesk.domain.dto.CreateAppUserRequestDto;
import com.marcomoretta.dungeondesk.domain.entity.AppUser;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper for the AppUser to and from Dto
 */
@Component
public class AppUserMapperImpl implements AppUserMapper {

    @Override
    public CreateAppUserRequest fromDto(CreateAppUserRequestDto dto) {
        return new CreateAppUserRequest(
                dto.name(),
                dto.secret()
        );
    }


    @Override
    public AppUserDto toDto(AppUser appUser) {
        return new AppUserDto(
                appUser.getUserId(),
                appUser.getUsername(),
                appUser.getCampaignList()
        );
    }


    @Override
    public List<AppUserDto> toDtoList(List<AppUser> appUserList) {
        return appUserList.stream().map(this::toDto).toList();
    }
}
