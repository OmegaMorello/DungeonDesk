package com.marcomoretta.dungeondesk.mapper;

import com.marcomoretta.dungeondesk.domain.dto.AppUserDto;
import com.marcomoretta.dungeondesk.domain.dto.request.CreateAppUserRequestDto;
import com.marcomoretta.dungeondesk.domain.dto.request.UpdateAppUserRequestDto;
import com.marcomoretta.dungeondesk.domain.entity.AppUser;
import com.marcomoretta.dungeondesk.domain.request.CreateAppUserRequest;
import com.marcomoretta.dungeondesk.domain.request.UpdateAppUserRequest;

import java.util.List;

/**
 * App User DTO mapper interface
 */
public interface AppUserMapper {

    /**
     * Maps a user create request from a dto
     *
     * @param dto Presentation layer request dto
     * @return The service layer request
     */
    CreateAppUserRequest fromCreateDto(CreateAppUserRequestDto dto);

    /**
     * Maps a user update request from a dto
     *
     * @param dto Presentation layer request dto
     * @return The service layer request
     */
    UpdateAppUserRequest fromUpdateDto(UpdateAppUserRequestDto dto, Long id);

    /**
     * Maps a user to a dto
     *
     * @param appUser Service layer user
     * @return Presentation layer user dto
     */
    AppUserDto toDto(AppUser appUser);

    /**
     * Maps a user list to a dto list
     *
     * @param appUserList Service layer user list
     * @return Presentation layer user list dto
     */
    List<AppUserDto> toDtoList(List<AppUser> appUserList);
}
