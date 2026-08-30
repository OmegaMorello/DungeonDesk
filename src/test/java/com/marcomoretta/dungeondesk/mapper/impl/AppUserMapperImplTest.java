package com.marcomoretta.dungeondesk.mapper.impl;

import com.marcomoretta.dungeondesk.domain.dto.AppUserDto;
import com.marcomoretta.dungeondesk.domain.dto.request.CreateAppUserRequestDto;
import com.marcomoretta.dungeondesk.domain.dto.request.UpdateAppUserRequestDto;
import com.marcomoretta.dungeondesk.domain.entity.AppUser;
import com.marcomoretta.dungeondesk.domain.request.CreateAppUserRequest;
import com.marcomoretta.dungeondesk.domain.request.UpdateAppUserRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AppUserMapperImplTest {

    private final AppUserMapperImpl appUserMapper = new AppUserMapperImpl();

    @Test
    void fromCreateDto() {
        // Arrange
        CreateAppUserRequestDto createAppUserRequestDto =
                new CreateAppUserRequestDto("Test", "Secret");

        // Act
        CreateAppUserRequest createAppUserRequest = appUserMapper.fromCreateDto(createAppUserRequestDto);

        // Assert
        assertEquals("Test", createAppUserRequest.username());
        assertEquals("Secret", createAppUserRequest.secret());
    }

    @Test
    void fromUpdateDto() {
        // Arrange
        UpdateAppUserRequestDto updateAppUserRequestDto =
                new UpdateAppUserRequestDto("User", "Test", "Secret");

        // Act
        UpdateAppUserRequest updateAppUserRequest = appUserMapper.fromUpdateDto(updateAppUserRequestDto, 2L);

        // Assert
        assertEquals("User", updateAppUserRequest.username());
        assertEquals("Test", updateAppUserRequest.currentSecret());
        assertEquals("Secret", updateAppUserRequest.newSecret());
        assertEquals(2, updateAppUserRequest.appUserId());
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
        assertEquals(10L, appUserDto.appUserId());
        assertEquals(appUser.getUsername(), appUserDto.username());
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
        assertEquals("User1", appUserDtos.getFirst().username());
        assertEquals("User2", appUserDtos.getLast().username());
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