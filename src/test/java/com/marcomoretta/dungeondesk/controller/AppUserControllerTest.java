package com.marcomoretta.dungeondesk.controller;

import com.marcomoretta.dungeondesk.auth.AuthSession;
import com.marcomoretta.dungeondesk.auth.LoginType;
import com.marcomoretta.dungeondesk.domain.dto.AppUserDto;
import com.marcomoretta.dungeondesk.domain.dto.request.UpdateAppUserRequestDto;
import com.marcomoretta.dungeondesk.domain.entity.AppUser;
import com.marcomoretta.dungeondesk.domain.request.UpdateAppUserRequest;
import com.marcomoretta.dungeondesk.exception.ResourcePermissionException;
import com.marcomoretta.dungeondesk.mapper.AppUserMapper;
import com.marcomoretta.dungeondesk.service.AppUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppUserControllerTest {

    private final static Long OWNER_ID = 1L;

    @Mock
    private AppUserService appUserService;
    @Mock
    private AppUserMapper appUserMapper;

    @InjectMocks
    private AppUserController controller;

    private AuthSession masterSession;
    private AuthSession playerSession;

    @BeforeEach
    void setup() {
        // Arrange
        masterSession = AuthSession.builder()
                .loginType(LoginType.MASTER).userId(OWNER_ID).displayName("DM").build();

        playerSession = AuthSession.builder()
                .loginType(LoginType.PLAYER).playerId(3L).displayName("Omega").build();
    }

    @Test
    void updateUser() {
        // Arrange
        UpdateAppUserRequestDto dto = new UpdateAppUserRequestDto("NewName", "old", "newsecret");
        UpdateAppUserRequest request = new UpdateAppUserRequest(OWNER_ID, "NewName", "old", "newsecret");
        AppUser updated = AppUser.builder().userId(OWNER_ID).username("NewName").build();

        when(appUserMapper.fromUpdateDto(dto, OWNER_ID)).thenReturn(request);
        when(appUserService.updateUser(request)).thenReturn(updated);
        when(appUserMapper.toDto(updated)).thenReturn(new AppUserDto(OWNER_ID, "NewName"));

        // Act - Assert
        assertEquals(HttpStatus.OK, controller.updateUser(masterSession, dto).getStatusCode());
    }

    @Test
    void updateUser_onlyMassterAllowed() {
        // Act - Assert
        assertThrows(ResourcePermissionException.class,
                () -> controller.updateUser(playerSession, null));

        verifyNoInteractions(appUserService);
    }
}