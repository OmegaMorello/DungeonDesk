package com.marcomoretta.dungeondesk.controller;

import com.marcomoretta.dungeondesk.auth.AuthInterceptor;
import com.marcomoretta.dungeondesk.auth.AuthSession;
import com.marcomoretta.dungeondesk.domain.dto.MapImageDto;
import com.marcomoretta.dungeondesk.domain.dto.MapStateDto;
import com.marcomoretta.dungeondesk.domain.dto.TokenDto;
import com.marcomoretta.dungeondesk.domain.dto.request.CreateOrResizeMapRequestDto;
import com.marcomoretta.dungeondesk.domain.dto.request.CreateTokenRequestDto;
import com.marcomoretta.dungeondesk.domain.dto.request.MoveTokenRequestDto;
import com.marcomoretta.dungeondesk.domain.entity.MapState;
import com.marcomoretta.dungeondesk.domain.entity.Token;
import com.marcomoretta.dungeondesk.event.GameEventStream;
import com.marcomoretta.dungeondesk.event.TokenMovedEvent;
import com.marcomoretta.dungeondesk.mapper.MapStateMapper;
import com.marcomoretta.dungeondesk.service.MapService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;

/**
 * Class that exposes REST APIs for the map and tokens on it
 */
@RestController
@RequestMapping("/api/v1/map")
public class MapController {

    private final MapService mapService;
    private final MapStateMapper mapStateMapper;
    private final GameEventStream gameEventStream;

    public MapController(MapService mapService, MapStateMapper mapStateMapper, GameEventStream gameEventStream) {
        this.mapService = mapService;
        this.mapStateMapper = mapStateMapper;
        this.gameEventStream = gameEventStream;
    }

    /**
     * Requests the map of the current campaign
     *
     * @param authSession The actual session
     * @return The map and its tokens [200 - OK]
     */
    @GetMapping
    public ResponseEntity<MapStateDto> getMap(
            @RequestAttribute(AuthInterceptor.SESSION_ATTRIBUTE) AuthSession authSession) {

        return ResponseEntity.ok(mapStateMapper.toDto(mapService.getMap(authSession)));
    }


    /**
     * Requests the creation or resize of the campaign map
     *
     * @param createOrResizeMapRequestDto The grid size and campaign the map belongs to
     * @param authSession                 The actual session
     * @return Teh saved map [200 - OK]
     */
    @PostMapping
    public ResponseEntity<MapStateDto> createOrResizeMap(
            @Valid @RequestBody CreateOrResizeMapRequestDto createOrResizeMapRequestDto,
            @RequestAttribute(AuthInterceptor.SESSION_ATTRIBUTE) AuthSession authSession) {

        authSession.requireMaster();

        MapState mapState = mapService.createOrResizeMap(
                createOrResizeMapRequestDto.campaignId(),
                createOrResizeMapRequestDto.gridRows(),
                createOrResizeMapRequestDto.gridColumns(),
                authSession
        );

        return ResponseEntity.ok(mapStateMapper.toDto(mapState));
    }


    /**
     * Request to create and place a new token on the map
     *
     * @param createTokenRequestDto The sheet the token belongs to, the type of token and its position
     * @param authSession           The actual session
     * @return The created token [201 - CREATED]
     */
    @PostMapping("/tokens")
    public ResponseEntity<TokenDto> addToken(
            @Valid @RequestBody CreateTokenRequestDto createTokenRequestDto,
            @RequestAttribute(AuthInterceptor.SESSION_ATTRIBUTE) AuthSession authSession) {

        authSession.requireMaster();

        Token token = mapService.addToken(
                createTokenRequestDto.sheetId(),
                createTokenRequestDto.tokenType(),
                createTokenRequestDto.posX(),
                createTokenRequestDto.posY(),
                authSession
        );

        TokenDto tokenDto = mapStateMapper.toDto(token);

        notifyMoved(tokenDto, authSession);

        return ResponseEntity.status(HttpStatus.CREATED).body(tokenDto);
    }


    /**
     * Request to move the token on the map
     *
     * @param tokenId             The id of the token to be moved
     * @param moveTokenRequestDto The target coordinates on the map
     * @param authSession         The actual session
     * @return The moved token [200 - OK]
     */
    @PutMapping("/tokens/{tokenId}")
    public ResponseEntity<TokenDto> moveToken(
            @PathVariable Long tokenId,
            @Valid @RequestBody MoveTokenRequestDto moveTokenRequestDto,
            @RequestAttribute(AuthInterceptor.SESSION_ATTRIBUTE) AuthSession authSession) {

        Token token = mapService.moveToken(
                tokenId,
                moveTokenRequestDto.posX(),
                moveTokenRequestDto.posY(),
                authSession
        );

        TokenDto tokenDto = mapStateMapper.toDto(token);

        notifyMoved(tokenDto, authSession);

        return ResponseEntity.ok(tokenDto);
    }


    /**
     * Request to remove a token from the map
     *
     * @param tokenId     The id of the token to be removed
     * @param authSession The actual session
     * @return A void response when removed [204 - NO CONTENT]
     */
    @DeleteMapping("/tokens/{tokenId}")
    public ResponseEntity<Void> deleteToken(
            @PathVariable Long tokenId,
            @RequestAttribute(AuthInterceptor.SESSION_ATTRIBUTE) AuthSession authSession) {

        authSession.requireMaster();
        mapService.deleteToken(tokenId, authSession);

        return ResponseEntity.noContent().build();
    }


    /**
     * Request to upload and replace (if existing) the map background
     *
     * @param mapFile     The uploaded image [PNG or JPEG]
     * @param authSession The actual session
     * @return A void response when stored [204 - NO CONTENT]
     */
    @PostMapping("/background")
    public ResponseEntity<Void> uploadMapBackground(
            @RequestParam("mapFile") MultipartFile mapFile,
            @RequestAttribute(AuthInterceptor.SESSION_ATTRIBUTE) AuthSession authSession) {

        authSession.requireMaster();
        mapService.storeBackground(mapFile, authSession);

        return ResponseEntity.noContent().build();
    }


    /**
     * Requests the map background image
     *
     * @param authSession The actual session
     * @return The image byte array [200 - OK]
     */
    @GetMapping("/background")
    public ResponseEntity<byte[]> getBackground(
            @RequestAttribute(AuthInterceptor.SESSION_ATTRIBUTE) AuthSession authSession) {

        MapImageDto mapImageDto = mapService.readBackground(authSession);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mapImageDto.contentType()))
                .body(mapImageDto.content());
    }


    /**
     * Publish the moved or placed token's position to every client
     *
     * @param tokenDto    The token id and position info
     * @param authSession The actual session
     */
    private void notifyMoved(TokenDto tokenDto, AuthSession authSession) {
        gameEventStream.notifyObservers(
                new TokenMovedEvent(
                        authSession.displayName(),
                        tokenDto.tokenId(),
                        tokenDto.posX(),
                        tokenDto.posY(),
                        Instant.now()
                )
        );
    }

}
