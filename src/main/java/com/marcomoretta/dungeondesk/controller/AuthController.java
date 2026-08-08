package com.marcomoretta.dungeondesk.controller;

import com.marcomoretta.dungeondesk.auth.AuthInterceptor;
import com.marcomoretta.dungeondesk.auth.AuthSession;
import com.marcomoretta.dungeondesk.auth.SessionStore;
import com.marcomoretta.dungeondesk.domain.dto.AppUserDto;
import com.marcomoretta.dungeondesk.domain.dto.AuthSessionDto;
import com.marcomoretta.dungeondesk.domain.dto.SessionInfoDto;
import com.marcomoretta.dungeondesk.domain.dto.request.CreateAppUserRequestDto;
import com.marcomoretta.dungeondesk.domain.dto.request.LoginRequestDto;
import com.marcomoretta.dungeondesk.domain.entity.AppUser;
import com.marcomoretta.dungeondesk.domain.request.CreateAppUserRequest;
import com.marcomoretta.dungeondesk.mapper.AppUserMapper;
import com.marcomoretta.dungeondesk.mapper.AuthMapper;
import com.marcomoretta.dungeondesk.service.AppUserService;
import com.marcomoretta.dungeondesk.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST API Controller to manage authentication
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthMapper authMapper;
    private final AuthService authService;
    private final SessionStore sessionStore;
    private final AppUserMapper appUserMapper;
    private final AppUserService appUserService;

    public AuthController(AuthMapper authMapper, AuthService authService, SessionStore sessionStore, AppUserMapper appUserMapper, AppUserService appUserService) {
        this.authMapper = authMapper;
        this.authService = authService;
        this.sessionStore = sessionStore;
        this.appUserMapper = appUserMapper;
        this.appUserService = appUserService;
    }

    /**
     * Requests to create a new user
     *
     * @param createAppUserRequestDto The dto container with name and secret
     * @return The correctly created user [201 - CREATED]
     */
    @PostMapping("/register")
    public ResponseEntity<AppUserDto> register(
            @Valid @RequestBody CreateAppUserRequestDto createAppUserRequestDto
    ) {
        CreateAppUserRequest createAppUserRequest = appUserMapper.fromCreateDto(createAppUserRequestDto);
        AppUser appUser = appUserService.createUser(createAppUserRequest);
        AppUserDto appUserDto = appUserMapper.toDto(appUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(appUserDto); //TODO: Check warning
    }

    /**
     * Requests to authenticate a user, either a Dungeon Master or Player
     *
     * @param loginRequestDto The dto containing login request details
     * @return The successfully created session [200 - OK]
     */
    @PostMapping("/login")
    public ResponseEntity<AuthSessionDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        AuthSession authSession = authService.login(
                loginRequestDto.loginType(),
                loginRequestDto.username(),
                loginRequestDto.secret());

        return ResponseEntity.ok(authMapper.toDto(authSession));
    }

    /**
     * Requests to log out a user, destroying its session
     *
     * @param authSession The session previously created
     * @return A void response if the user was correctly found and logged out [204 - NO CONTENT]
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestAttribute(AuthInterceptor.SESSION_ATTRIBUTE) AuthSession authSession) {

        sessionStore.remove(authSession.token());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<SessionInfoDto> me(
            @RequestAttribute(AuthInterceptor.SESSION_ATTRIBUTE) AuthSession authSession) {

        return ResponseEntity.ok(authMapper.toSessionInfoDto(authSession));
    }

//    @GetMapping("/sessions/players")
//    public ResponseEntity<List<PlayerDto>> getPlayerList() {
//
//    }
}
