package com.marcomoretta.dungeondesk.controller;

import com.marcomoretta.dungeondesk.auth.AuthInterceptor;
import com.marcomoretta.dungeondesk.auth.AuthSession;
import com.marcomoretta.dungeondesk.domain.dto.GameSessionDto;
import com.marcomoretta.dungeondesk.domain.dto.request.CreateGameSessionRequestDto;
import com.marcomoretta.dungeondesk.domain.dto.request.UpdateGameSessionRequestDto;
import com.marcomoretta.dungeondesk.domain.entity.GameSession;
import com.marcomoretta.dungeondesk.domain.request.CreateGameSessionRequest;
import com.marcomoretta.dungeondesk.domain.request.UpdateGameSessionRequest;
import com.marcomoretta.dungeondesk.exception.GameSessionNotFoundException;
import com.marcomoretta.dungeondesk.mapper.GameSessionMapper;
import com.marcomoretta.dungeondesk.service.GameSessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Class that exposes REST APIs for game sessions management.
 * Every endpoint here is reserved to the Dungeon Master: the responses carry the join
 * code, which is the credential the players use to connect.
 */
@RestController
@RequestMapping("/api/v1")
public class GameSessionController {

    private final GameSessionService gameSessionService;
    private final GameSessionMapper gameSessionMapper;

    public GameSessionController(GameSessionService gameSessionService,
                                 GameSessionMapper gameSessionMapper) {
        this.gameSessionService = gameSessionService;
        this.gameSessionMapper = gameSessionMapper;
    }

    /**
     * Opens a new session on a campaign
     *
     * @param campaignId  The campaign hosting the session
     * @param dto         The session details, join code included
     * @param authSession The client active session
     * @return The opened session [201 - CREATED]
     */
    @PostMapping("/campaigns/{campaignId}/sessions")
    public ResponseEntity<GameSessionDto> createSession(
            @PathVariable Long campaignId,
            @Valid @RequestBody CreateGameSessionRequestDto dto,
            @RequestAttribute(AuthInterceptor.SESSION_ATTRIBUTE) AuthSession authSession) {

        authSession.requireMaster();

        CreateGameSessionRequest request = gameSessionMapper.fromCreateDto(dto, campaignId);
        GameSession gameSession = gameSessionService.createNewSession(request, authSession.userId());

        return ResponseEntity.status(HttpStatus.CREATED).body(gameSessionMapper.toDto(gameSession));
    }

    /**
     * Requests the session currently running
     *
     * @param authSession The client active session
     * @return The running session [200 - OK], or 404 when none is open
     */
    @GetMapping("/sessions/active")
    public ResponseEntity<GameSessionDto> getActiveSession(
            @RequestAttribute(AuthInterceptor.SESSION_ATTRIBUTE) AuthSession authSession) {

        authSession.requireMaster();

        // Here the absence IS an error: the master explicitly asked for the running
        // session. The public login screen uses the roster endpoint instead, where an
        // empty result is a legitimate answer.
        GameSession gameSession = gameSessionService.getActiveSession()
                .orElseThrow(() -> new GameSessionNotFoundException("No session is currently running"));

        return ResponseEntity.ok(gameSessionMapper.toDto(gameSession));
    }

    /**
     * Requests a join code change on a running session
     *
     * @param sessionId   The session to update
     * @param dto         The updated join code
     * @param authSession The client active session
     * @return The updated session [200 - OK]
     */
    @PutMapping("/sessions/{sessionId}")
    public ResponseEntity<GameSessionDto> updateSession(
            @PathVariable Long sessionId,
            @Valid @RequestBody UpdateGameSessionRequestDto dto,
            @RequestAttribute(AuthInterceptor.SESSION_ATTRIBUTE) AuthSession authSession) {

        authSession.requireMaster();

        UpdateGameSessionRequest request = gameSessionMapper.fromUpdateDto(dto, sessionId);
        GameSession gameSession = gameSessionService.updateSession(request, authSession.userId());

        return ResponseEntity.ok(gameSessionMapper.toDto(gameSession));
    }

    /**
     * Closes a running session.
     * A POST and not a DELETE: the session is not removed, it is marked as ended and
     * stays as history for the campaign.
     *
     * @param sessionId   The session to close
     * @param authSession The client active session
     * @return The closed session [200 - OK]
     */
    @PostMapping("/sessions/{sessionId}/close")
    public ResponseEntity<GameSessionDto> closeSession(
            @PathVariable Long sessionId,
            @RequestAttribute(AuthInterceptor.SESSION_ATTRIBUTE) AuthSession authSession) {

        authSession.requireMaster();

        GameSession gameSession = gameSessionService.closeSession(sessionId, authSession.userId());

        return ResponseEntity.ok(gameSessionMapper.toDto(gameSession));
    }
}
