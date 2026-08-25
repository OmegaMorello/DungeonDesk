package com.marcomoretta.dungeondesk.controller;

import com.marcomoretta.dungeondesk.auth.AuthInterceptor;
import com.marcomoretta.dungeondesk.auth.AuthSession;
import com.marcomoretta.dungeondesk.auth.SessionStore;
import com.marcomoretta.dungeondesk.domain.dto.AppUserDto;
import com.marcomoretta.dungeondesk.domain.dto.AuthSessionDto;
import com.marcomoretta.dungeondesk.domain.dto.SessionPlayerDto;
import com.marcomoretta.dungeondesk.domain.dto.request.CreateAppUserRequestDto;
import com.marcomoretta.dungeondesk.domain.dto.request.LoginRequestDto;
import com.marcomoretta.dungeondesk.domain.entity.AppUser;
import com.marcomoretta.dungeondesk.domain.entity.Player;
import com.marcomoretta.dungeondesk.domain.request.CreateAppUserRequest;
import com.marcomoretta.dungeondesk.mapper.AppUserMapper;
import com.marcomoretta.dungeondesk.mapper.AuthMapper;
import com.marcomoretta.dungeondesk.mapper.GameSessionMapper;
import com.marcomoretta.dungeondesk.service.AppUserService;
import com.marcomoretta.dungeondesk.service.AuthService;
import com.marcomoretta.dungeondesk.service.GameSessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    private final GameSessionMapper gameSessionMapper;
    private final GameSessionService gameSessionService;

    public AuthController(AuthMapper authMapper, AuthService authService, SessionStore sessionStore, AppUserMapper appUserMapper, AppUserService appUserService, GameSessionMapper gameSessionMapper, GameSessionService gameSessionService) {
        this.authMapper = authMapper;
        this.authService = authService;
        this.sessionStore = sessionStore;
        this.appUserMapper = appUserMapper;
        this.appUserService = appUserService;
        this.gameSessionMapper = gameSessionMapper;
        this.gameSessionService = gameSessionService;
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
    public ResponseEntity<AuthSessionDto> login(
            @Valid @RequestBody LoginRequestDto loginRequestDto) {

        AuthSession authSession = authService.login(
                loginRequestDto.loginType(),
                loginRequestDto.username(),
                loginRequestDto.secret());

        ResponseCookie responseCookie = cookieBuilder(authSession.token())
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(authMapper.toDto(authSession));
    }

    /**
     * Requests to log out a user, destroying its session
     *
     * @return A void response if the user was correctly found and logged out [204 - NO CONTENT]
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestAttribute(AuthInterceptor.SESSION_ATTRIBUTE) AuthSession authSession) {

        sessionStore.remove(authSession.token());

        ResponseCookie responseCookie = cookieBuilder("")
                .maxAge(0)
                .build();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .build();
    }

    @GetMapping("/me")
    public ResponseEntity<AuthSessionDto> me(
            @RequestAttribute(AuthInterceptor.SESSION_ATTRIBUTE) AuthSession authSession) {

        return ResponseEntity.ok(authMapper.toDto(authSession));
    }

    /**
     * Requests the roster of the running session, so a player can pick their name.
     * TODO: hide the players already connected once the claim is made thread safe.
     *
     * @return The available names, empty list when no session is running [200 - OK]
     */
    @GetMapping("/session/players")
    public ResponseEntity<List<SessionPlayerDto>> getAvailablePlayers() {

        List<Player> roster = gameSessionService.getActiveSessionRoster();

        return ResponseEntity.ok(gameSessionMapper.toSessionPlayerDtoList(roster));
    }


    /**
     * Partial builder for the cookie
     *
     * @param value The value to assign
     * @return The partial cookie builder, to be completed by its caller
     */
    private ResponseCookie.ResponseCookieBuilder cookieBuilder(String value) {
        return ResponseCookie.from(AuthInterceptor.COOKIE_NAME, value)
                .httpOnly(true)
                .sameSite("Strict")
                .path("/");
    }
}
