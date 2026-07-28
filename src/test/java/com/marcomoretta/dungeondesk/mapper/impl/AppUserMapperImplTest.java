package com.marcomoretta.dungeondesk.mapper.impl;

import com.marcomoretta.dungeondesk.domain.dto.AppUserDto;
import com.marcomoretta.dungeondesk.domain.dto.CreateAppUserRequestDto;
import com.marcomoretta.dungeondesk.domain.entity.AppUser;
import com.marcomoretta.dungeondesk.domain.request.CreateAppUserRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AppUserMapperImplTest {

    private final AppUserMapperImpl appUserMapper = new AppUserMapperImpl();

    @Test
    void fromDto() {
        // Arrange
        CreateAppUserRequestDto createAppUserRequestDto =
                new CreateAppUserRequestDto(
                        "Test", "Secret");
        // Act
        CreateAppUserRequest createAppUserRequest = appUserMapper.fromDto(createAppUserRequestDto);

        // Assert
        assertEquals(createAppUserRequestDto.name(), createAppUserRequest.name());
        assertEquals(createAppUserRequestDto.secret(),createAppUserRequest.secret());

    }

    @Test
    void toDto() {
        // Arrange
        AppUser appUser = AppUser.builder()
                .userId(10L)
                .username("UserTest")
                .hashSecret("SecretTest")
                .build();

        // Act
        AppUserDto appUserDto = appUserMapper.toDto(appUser);

        // Assert
        assertEquals(10L, appUserDto.id());
        assertEquals(appUser.getUsername(), appUserDto.name());
        assertNotNull(appUserDto.campaignList());
        assertTrue(appUserDto.campaignList().isEmpty());

    }

    @Test
    void toDtoList() {
        // Arrange
        AppUser appUser1 = AppUser.builder()
                .userId(1L)
                .username("User1")
                .hashSecret("Secret1")
                .build();
        AppUser appUser2 = AppUser.builder()
                .userId(2L)
                .username("User2")
                .hashSecret("Secret2")
                .build();

        List<AppUser> appUserList = List.of(appUser1, appUser2);

        // Act
        List<AppUserDto> appUserDtos = appUserMapper.toDtoList(appUserList);

        // Assert
        assertEquals(2, appUserDtos.size());
        assertEquals(appUser1.getUsername(), appUserDtos.getFirst().name());
        assertEquals(appUser2.getUsername(), appUserDtos.getLast().name());
    }

    @Test
    void toDtoListEmpty() {
        // Act
        List<AppUserDto> appUserDtos = appUserMapper.toDtoList(List.of());

        // Assert
        assertNotNull(appUserDtos);
        assertTrue(appUserDtos.isEmpty());

    }
}