package com.marcomoretta.dungeondesk.mapper;

import com.marcomoretta.dungeondesk.domain.request.CreateAppUserRequest;
import com.marcomoretta.dungeondesk.domain.dto.AppUserDto;
import com.marcomoretta.dungeondesk.domain.dto.CreateAppUserRequestDto;
import com.marcomoretta.dungeondesk.domain.entity.AppUser;

import java.util.List;

/**
 * App User DTO mapper
 */
public interface AppUserMapper {

    CreateAppUserRequest fromDto(CreateAppUserRequestDto dto);

    AppUserDto toDto(AppUser appUser);

    List<AppUserDto> toDtoList(List<AppUser> appUserList);
}
