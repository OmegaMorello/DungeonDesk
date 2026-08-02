package com.marcomoretta.dungeondesk.mapper;

import com.marcomoretta.dungeondesk.domain.dto.AppUserDto;
import com.marcomoretta.dungeondesk.domain.dto.request.CreateAppUserRequestDto;
import com.marcomoretta.dungeondesk.domain.dto.request.UpdateAppUserRequestDto;
import com.marcomoretta.dungeondesk.domain.entity.AppUser;
import com.marcomoretta.dungeondesk.domain.request.CreateAppUserRequest;
import com.marcomoretta.dungeondesk.domain.request.UpdateAppUserRequest;

import java.util.List;

/**
 * App User DTO mapper
 */
public interface AppUserMapper {

    /**
     * Maps a user create request from a dto
     * @param dto presentation layer request dto
     * @return the service layer request
     */
    CreateAppUserRequest fromCreateDto(CreateAppUserRequestDto dto);

    /**
     * Maps a user update request from a dto
     * @param dto presentation layer request dto
     * @return the service layer request
     */
    UpdateAppUserRequest fromUpdateDto(UpdateAppUserRequestDto dto);

    /**
     * Maps a user to a dto
     * @param appUser service layer user
     * @return presentation layer user dto
     */
    AppUserDto toDto(AppUser appUser);

    /**
     * Maps a user list to a dto list
     * @param appUserList service layer user list
     * @return presentation layer user list dto
     */
    List<AppUserDto> toDtoList(List<AppUser> appUserList);
}
