package com.marcomoretta.dungeondesk.mapper.impl;

import com.marcomoretta.dungeondesk.domain.dto.AppUserDto;
import com.marcomoretta.dungeondesk.domain.dto.request.CreateAppUserRequestDto;
import com.marcomoretta.dungeondesk.domain.dto.request.UpdateAppUserRequestDto;
import com.marcomoretta.dungeondesk.domain.entity.AppUser;
import com.marcomoretta.dungeondesk.domain.request.CreateAppUserRequest;
import com.marcomoretta.dungeondesk.domain.request.UpdateAppUserRequest;
import com.marcomoretta.dungeondesk.mapper.AppUserMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper for AppUser DTOs
 */
@Component
public class AppUserMapperImpl implements AppUserMapper {

    @Override
    public CreateAppUserRequest fromCreateDto(CreateAppUserRequestDto dto) {
        return new CreateAppUserRequest(
                dto.username(),
                dto.secret()
        );
    }

    @Override
    public UpdateAppUserRequest fromUpdateDto(UpdateAppUserRequestDto dto, Long id) {
        return new UpdateAppUserRequest(
                id,
                dto.username(),
                dto.currentSecret(),
                dto.newSecret()
        );
    }


    @Override
    public AppUserDto toDto(AppUser appUser) {
        return new AppUserDto(
                appUser.getUserId(),
                appUser.getUsername()
        );
    }


    @Override
    public List<AppUserDto> toDtoList(List<AppUser> appUserList) {
        return appUserList.stream().map(this::toDto).toList();
    }
}
