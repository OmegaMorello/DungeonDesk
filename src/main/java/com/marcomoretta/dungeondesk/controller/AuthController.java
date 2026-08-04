package com.marcomoretta.dungeondesk.controller;

import com.marcomoretta.dungeondesk.auth.AuthInterceptor;
import com.marcomoretta.dungeondesk.auth.AuthSession;
import com.marcomoretta.dungeondesk.auth.SessionStore;
import com.marcomoretta.dungeondesk.domain.dto.AuthSessionDto;
import com.marcomoretta.dungeondesk.domain.dto.request.LoginRequestDto;
import com.marcomoretta.dungeondesk.mapper.AuthMapper;
import com.marcomoretta.dungeondesk.service.AuthService;
import jakarta.validation.Valid;
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

    public AuthController(AuthMapper authMapper, AuthService authService, SessionStore sessionStore) {
        this.authMapper = authMapper;
        this.authService = authService;
        this.sessionStore = sessionStore;
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
    public ResponseEntity<Void> logout(@RequestAttribute(AuthInterceptor.SESSION_ATTRIBUTE) AuthSession authSession) {
        sessionStore.remove(authSession.token());
        return ResponseEntity.noContent().build();
    }
}
