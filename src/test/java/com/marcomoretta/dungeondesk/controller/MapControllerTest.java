package com.marcomoretta.dungeondesk.controller;

import com.marcomoretta.dungeondesk.auth.AuthSession;
import com.marcomoretta.dungeondesk.auth.LoginType;

import com.marcomoretta.dungeondesk.domain.dto.MapImageDto;
import com.marcomoretta.dungeondesk.domain.dto.MapStateDto;
import com.marcomoretta.dungeondesk.domain.dto.TokenDto;
import com.marcomoretta.dungeondesk.domain.dto.request.CreateOrResizeMapRequestDto;
import com.marcomoretta.dungeondesk.domain.dto.request.MoveTokenRequestDto;
import com.marcomoretta.dungeondesk.domain.entity.MapState;
import com.marcomoretta.dungeondesk.domain.entity.Token;
import com.marcomoretta.dungeondesk.domain.entity.TokenType;
import com.marcomoretta.dungeondesk.event.GameEventStream;
import com.marcomoretta.dungeondesk.event.MapChangedEvent;
import com.marcomoretta.dungeondesk.event.TokenMovedEvent;
import com.marcomoretta.dungeondesk.event.TokenRemovedEvent;
import com.marcomoretta.dungeondesk.exception.ResourcePermissionException;

import com.marcomoretta.dungeondesk.mapper.MapStateMapper;
import com.marcomoretta.dungeondesk.service.MapService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MapControllerTest {

    private final static Long CAMPAIGN_ID = 2L;
    private final static Long TOKEN_ID = 5L;

    @Mock
    private MapService mapService;
    @Mock
    private MapStateMapper mapStateMapper;
    @Mock
    private GameEventStream gameEventStream;

    @InjectMocks
    private MapController controller;

    private AuthSession masterSession;
    private AuthSession playerSession;

    @BeforeEach
    void setup() {
        // Arrange
        masterSession = AuthSession.builder()
                .loginType(LoginType.MASTER).userId(1L).displayName("DM").build();

        playerSession = AuthSession.builder()
                .loginType(LoginType.PLAYER).playerId(3L)
                .campaignId(CAMPAIGN_ID).displayName("Omega").build();
    }

    @Test
    void createOrResizeMap() {
        // Arrange
        CreateOrResizeMapRequestDto dto = new CreateOrResizeMapRequestDto(CAMPAIGN_ID, 8, 12);
        MapState mapState = MapState.builder().mapStateId(6L).gridRows(8).gridColumns(12).build();

        when(mapService.createOrResizeMap(CAMPAIGN_ID, 8, 12, masterSession)).thenReturn(mapState);

        // Act
        ResponseEntity<MapStateDto> response = controller.createOrResizeMap(dto, masterSession);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(gameEventStream).notifyObservers(any(MapChangedEvent.class));
    }

    @Test
    void createOrResizeMap_onlyMasterAllowed() {
        // Arrange
        CreateOrResizeMapRequestDto dto = new CreateOrResizeMapRequestDto(CAMPAIGN_ID, 8, 12);

        // Act - Assert
        assertThrows(ResourcePermissionException.class,
                () -> controller.createOrResizeMap(dto, playerSession));

        verifyNoInteractions(mapService, gameEventStream);
    }

    @Test
    void moveToken() {
        // Arrange
        MoveTokenRequestDto dto = new MoveTokenRequestDto(5, 6);
        Token token = Token.builder().tokenId(TOKEN_ID).posX(5).posY(6).build();
        TokenDto tokenDto = new TokenDto(TOKEN_ID, 4L, "Luigi", TokenType.PC, 5, 6);

        when(mapService.moveToken(TOKEN_ID, 5, 6, playerSession)).thenReturn(token);
        when(mapStateMapper.toDto(token)).thenReturn(tokenDto);

        // Act - Assert
        assertEquals(HttpStatus.OK,
                controller.moveToken(TOKEN_ID, dto, playerSession).getStatusCode());

        verify(gameEventStream).notifyObservers(any(TokenMovedEvent.class));
    }

    @Test
    void deleteToken() {
        // Act - Assert
        assertEquals(HttpStatus.NO_CONTENT,
                controller.deleteToken(TOKEN_ID, masterSession).getStatusCode());

        verify(mapService).deleteToken(TOKEN_ID, masterSession);
        verify(gameEventStream).notifyObservers(any(TokenRemovedEvent.class));
    }

    @Test
    void getBackground() {
        // Arrange
        when(mapService.readBackground(playerSession))
                .thenReturn(new MapImageDto("bytes".getBytes(), "image/png"));

        // Act
        ResponseEntity<byte[]> response = controller.getBackground(playerSession);

        // Assert
        assertEquals(MediaType.IMAGE_PNG, response.getHeaders().getContentType());
    }
}