package com.marcomoretta.dungeondesk.controller;

import com.marcomoretta.dungeondesk.auth.AuthSession;
import com.marcomoretta.dungeondesk.auth.LoginType;
import com.marcomoretta.dungeondesk.auth.SessionStore;
import com.marcomoretta.dungeondesk.domain.dto.AppUserDto;
import com.marcomoretta.dungeondesk.domain.dto.AuthSessionDto;
import com.marcomoretta.dungeondesk.domain.dto.request.CreateAppUserRequestDto;
import com.marcomoretta.dungeondesk.domain.dto.request.LoginRequestDto;
import com.marcomoretta.dungeondesk.domain.entity.AppUser;
import com.marcomoretta.dungeondesk.domain.request.CreateAppUserRequest;
import com.marcomoretta.dungeondesk.mapper.AppUserMapper;
import com.marcomoretta.dungeondesk.mapper.AuthMapper;
import com.marcomoretta.dungeondesk.mapper.GameSessionMapper;
import com.marcomoretta.dungeondesk.service.AppUserService;
import com.marcomoretta.dungeondesk.service.AuthService;
import com.marcomoretta.dungeondesk.service.GameSessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private final static Long OWNER_ID = 1L;

    @Mock
    private AuthMapper authMapper;
    @Mock
    private AuthService authService;
    @Mock
    private SessionStore sessionStore;
    @Mock
    private AppUserMapper appUserMapper;
    @Mock
    private AppUserService appUserService;
    @Mock
    private GameSessionMapper gameSessionMapper;
    @Mock
    private GameSessionService gameSessionService;

    @InjectMocks
    private AuthController controller;

    private AuthSession masterSession;
    private AuthSessionDto masterSessionDto;

    @BeforeEach
    void setup() {
        // Arrange
        masterSession = AuthSession.builder()
                .token("cookie-token")
                .loginType(LoginType.MASTER)
                .userId(OWNER_ID)
                .displayName("DM")
                .build();

        masterSessionDto = new AuthSessionDto(LoginType.MASTER, "DM", null, null, null);
    }

    @Test
    void login() {
        // Arrange
        LoginRequestDto dto = new LoginRequestDto(LoginType.MASTER, "DM", "secret");

        when(authService.login(LoginType.MASTER, "DM", "secret")).thenReturn(masterSession);
        when(authMapper.toDto(masterSession)).thenReturn(masterSessionDto);

        // Act
        ResponseEntity<AuthSessionDto> response = controller.login(dto);
        String cookie = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(cookie.contains("cookie-token"));
        assertTrue(cookie.contains("HttpOnly"));
        assertTrue(cookie.contains("SameSite=Strict"));
    }

    @Test
    void logout() {
        // Act
        ResponseEntity<Void> response = controller.logout(masterSession);
        String cookie = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertTrue(cookie.contains("Max-Age=0")); // What tells the browser to forget about the cookie
        verify(sessionStore).remove("cookie-token");
    }

    @Test
    void getMe() {
        // Arrange
        when(authMapper.toDto(masterSession)).thenReturn(masterSessionDto);

        // Act - Assert
        assertEquals("DM", controller.getMe(masterSession).getBody().displayName());
    }

    @Test
    void getSessionPlayers_emptyWithNoSession() {
        // Arrange
        when(gameSessionService.getActiveSessionRoster()).thenReturn(List.of());
        when(gameSessionMapper.toSessionPlayerDtoList(List.of())).thenReturn(List.of());

        // Act - Assert
        assertTrue(controller.getSessionPlayers().getBody().isEmpty());
    }

    @Test
    void register() {
        // Arrange
        CreateAppUserRequestDto dto = new CreateAppUserRequestDto("DM", "secret12");
        CreateAppUserRequest request = new CreateAppUserRequest("DM", "secret12");
        AppUser created = AppUser.builder().userId(OWNER_ID).username("DM").build();

        when(appUserMapper.fromCreateDto(dto)).thenReturn(request);
        when(appUserService.createUser(request)).thenReturn(created);
        when(appUserMapper.toDto(created)).thenReturn(new AppUserDto(OWNER_ID, "DM"));

        // Act - Assert
        assertEquals(HttpStatus.CREATED, controller.register(dto).getStatusCode());
    }
}