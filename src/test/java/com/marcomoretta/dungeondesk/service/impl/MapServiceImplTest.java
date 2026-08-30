package com.marcomoretta.dungeondesk.service.impl;

import com.marcomoretta.dungeondesk.auth.AuthSession;
import com.marcomoretta.dungeondesk.auth.LoginType;
import com.marcomoretta.dungeondesk.domain.dto.MapImageDto;
import com.marcomoretta.dungeondesk.domain.entity.*;
import com.marcomoretta.dungeondesk.exception.MapImageException;
import com.marcomoretta.dungeondesk.exception.MapStateNotFoundException;
import com.marcomoretta.dungeondesk.exception.TokenNotFoundException;
import com.marcomoretta.dungeondesk.exception.TokenPermissionException;
import com.marcomoretta.dungeondesk.repository.MapStateRepository;
import com.marcomoretta.dungeondesk.repository.SheetRepository;
import com.marcomoretta.dungeondesk.repository.TokenRepository;
import com.marcomoretta.dungeondesk.service.CampaignService;
import com.marcomoretta.dungeondesk.service.GameSessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MapServiceImplTest {

    // Using fixed ids for ease of use
    private final static Long OWNER_ID = 1L;
    private final static Long CAMPAIGN_ID = 2L;
    private final static Long PLAYER_ID = 3L;
    private final static Long SHEET_ID = 4L;
    private final static Long TOKEN_ID = 5L;
    private final static Long MAP_ID = 6L;

    @TempDir
    Path imageDirectory;

    @Mock
    private MapStateRepository mapStateRepository;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private SheetRepository sheetRepository;
    @Mock
    private CampaignService campaignService;
    @Mock
    private GameSessionService gameSessionService;

    private MapServiceImpl mapService;

    private AppUser owner;
    private Campaign campaign;
    private Player player;
    private MapState mapState;

    private AuthSession masterSession;
    private AuthSession playerSession;

    @BeforeEach
    void setup() {
        // Arrange

        // The image directory comes from @Value, so @InjectMocks cannot fill it
        mapService = new MapServiceImpl(mapStateRepository, tokenRepository, sheetRepository,
                campaignService, gameSessionService, imageDirectory.toString());

        owner = AppUser.builder().userId(OWNER_ID).username("DM").build();
        campaign = Campaign.builder().campaignId(CAMPAIGN_ID).name("Campaign1").owner(owner).build();
        player = Player.builder().playerId(PLAYER_ID).name("Omega").campaign(campaign).build();

        mapState = MapState.builder()
                .mapStateId(MAP_ID)
                .campaign(campaign)
                .gridColumns(20)
                .gridRows(10)
                .build();

        masterSession = AuthSession.builder()
                .loginType(LoginType.MASTER)
                .userId(OWNER_ID)
                .displayName(owner.getUsername())
                .build();

        playerSession = AuthSession.builder()
                .loginType(LoginType.PLAYER)
                .playerId(PLAYER_ID)
                .campaignId(CAMPAIGN_ID)
                .displayName(player.getName())
                .build();
    }

    @Test
    void createOrResizeMap() {
        // Arrange
        when(mapStateRepository.findByCampaign_CampaignId(CAMPAIGN_ID)).thenReturn(Optional.empty());
        when(campaignService.getCampaign(CAMPAIGN_ID)).thenReturn(campaign);
        when(mapStateRepository.save(any())).thenAnswer(call -> call.getArgument(0));

        // Act
        MapState created = mapService.createOrResizeMap(CAMPAIGN_ID, 8, 12, masterSession);

        // Assert
        assertEquals(8, created.getGridRows());
        assertEquals(12, created.getGridColumns());
        assertEquals(campaign, created.getCampaign());
    }

    @Test
    void createOrResizeMap_repositionTokensWhenOutOfBounds() {
        // Arrange
        Token outside = token(20, 9);
        Token inside = token(2, 3);
        mapState.addToken(outside);
        mapState.addToken(inside);

        when(mapStateRepository.findByCampaign_CampaignId(CAMPAIGN_ID)).thenReturn(Optional.of(mapState));
        when(mapStateRepository.save(any())).thenAnswer(call -> call.getArgument(0));

        // Act
        mapService.createOrResizeMap(CAMPAIGN_ID, 8, 10, masterSession);

        // Assert
        assertEquals(10, outside.getPosX());
        assertEquals(8, outside.getPosY());
        assertEquals(2, inside.getPosX());
        assertEquals(3, inside.getPosY());
    }

    @Test
    void addToken() {
        // Arrange
        CharacterSheet sheet = characterSheet();
        activeSession();
        when(mapStateRepository.findByCampaign_CampaignId(CAMPAIGN_ID)).thenReturn(Optional.of(mapState));
        when(sheetRepository.findById(SHEET_ID)).thenReturn(Optional.of(sheet));
        when(tokenRepository.existsBySheet_SheetId(SHEET_ID)).thenReturn(false);

        // Act
        Token token = mapService.addToken(SHEET_ID, TokenType.PC, 3, 4, masterSession);

        // Assert
        assertEquals(3, token.getPosX());
        assertEquals(sheet, token.getSheet());
        assertTrue(mapState.getTokenList().contains(token));
        verify(mapStateRepository).save(mapState);
    }

    @Test
    void addToken_cannotAddSecondTokenForSameCreature() {
        // Arrange
        activeSession();
        when(mapStateRepository.findByCampaign_CampaignId(CAMPAIGN_ID)).thenReturn(Optional.of(mapState));
        when(sheetRepository.findById(SHEET_ID)).thenReturn(Optional.of(characterSheet()));
        when(tokenRepository.existsBySheet_SheetId(SHEET_ID)).thenReturn(true);

        // Act - Assert
        assertThrows(TokenPermissionException.class,
                () -> mapService.addToken(SHEET_ID, TokenType.PC, 3, 4, masterSession));

        verify(mapStateRepository, never()).save(any()); // Verify that nothing is saved
    }

    @Test
    void addToken_mustBeInBounds() {
        // Arrange
        activeSession();
        when(mapStateRepository.findByCampaign_CampaignId(CAMPAIGN_ID)).thenReturn(Optional.of(mapState));

        // Act - Assert
        assertThrows(TokenPermissionException.class,
                () -> mapService.addToken(SHEET_ID, TokenType.PC, 50, 4, masterSession));

        verifyNoInteractions(sheetRepository); // Verify that the check comes before saving
    }

    @Test
    void moveToken_masterCanMoveAnyToken() {
        // Arrange
        Token token = token(1, 1);
        mapState.addToken(token);
        when(tokenRepository.findById(TOKEN_ID)).thenReturn(Optional.of(token));

        // Act
        Token moved = mapService.moveToken(TOKEN_ID, 5, 6, masterSession);

        // Assert
        assertEquals(5, moved.getPosX());
        assertEquals(6, moved.getPosY());
    }

    @Test
    void moveToken_playerCanOnlyMoveOwnToken() {
        // Arrange
        Token token = token(1, 1);
        // The token belongs to a sheet assigned to someone else
        ((CharacterSheet) token.getSheet())
                .setPlayer(Player.builder().playerId(18L).name("Toad").campaign(campaign).build());
        mapState.addToken(token);
        when(tokenRepository.findById(TOKEN_ID)).thenReturn(Optional.of(token));

        // Act - Assert
        assertThrows(TokenPermissionException.class,
                () -> mapService.moveToken(TOKEN_ID, 5, 6, playerSession));

        assertEquals(1, token.getPosX());
    }

    @Test
    void moveToken_mustBeInBounds() {
        // Arrange
        Token token = token(1, 1);
        mapState.addToken(token);
        when(tokenRepository.findById(TOKEN_ID)).thenReturn(Optional.of(token));

        // Act - Assert
        assertThrows(TokenPermissionException.class,
                () -> mapService.moveToken(TOKEN_ID, 21, 6, masterSession));
    }

    @Test
    void deleteToken() {
        // Arrange
        Token token = token(1, 1);
        mapState.addToken(token);
        when(tokenRepository.findById(TOKEN_ID)).thenReturn(Optional.of(token));

        // Act
        mapService.deleteToken(TOKEN_ID, masterSession);

        // Assert
        assertFalse(mapState.getTokenList().contains(token));
        verify(tokenRepository).delete(token);
    }

    @Test
    void findToken_missing() {
        // Arrange
        when(tokenRepository.findById(TOKEN_ID)).thenReturn(Optional.empty());

        // Act - Assert
        assertThrows(TokenNotFoundException.class,
                () -> mapService.deleteToken(TOKEN_ID, masterSession));
    }

    @Test
    void getMap_masterWithNoSession() {
        // Arrange
        when(gameSessionService.getActiveSession()).thenReturn(Optional.empty());

        // Act - Assert
        assertThrows(MapStateNotFoundException.class, () -> mapService.getMap(masterSession));
    }

    @Test
    void storeBackground_onlyPngAndJpegAllowed() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "mapFile", "map.txt", "text/plain", "no image".getBytes());

        // Act - Assert
        assertThrows(MapImageException.class,
                () -> mapService.storeBackground(file, masterSession));
        verifyNoInteractions(mapStateRepository); // Verify that the check comes before saving
    }

    @Test
    void storeBackground_saveTheFileAndGetItBack() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "mapFile", "map.png", "image/png", "fake png bytes".getBytes());

        when(mapStateRepository.findByCampaign_CampaignId(CAMPAIGN_ID)).thenReturn(Optional.of(mapState));

        // Act
        mapService.storeBackground(file, playerSession);

        // Assert
        assertEquals("map-" + MAP_ID + ".img", mapState.getBackgroundUrl());
        assertEquals("image/png", mapState.getBackgroundContentType());
        assertTrue(imageDirectory.resolve(mapState.getBackgroundUrl()).toFile().exists());
    }

    @Test
    void readBackground() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "mapFile", "map.png", "image/png", "fake png bytes".getBytes());

        when(mapStateRepository.findByCampaign_CampaignId(CAMPAIGN_ID)).thenReturn(Optional.of(mapState));
        mapService.storeBackground(file, playerSession);

        // Act
        MapImageDto image = mapService.readBackground(playerSession);

        // Assert
        assertArrayEquals("fake png bytes".getBytes(), image.content());
        assertEquals("image/png", image.contentType());
    }

    @Test
    void readBackground_empty() {
        // Arrange
        when(mapStateRepository.findByCampaign_CampaignId(CAMPAIGN_ID)).thenReturn(Optional.of(mapState));

        // Act - Assert
        assertThrows(MapStateNotFoundException.class,
                () -> mapService.readBackground(playerSession));
    }


    // ----- HELPERS -----

    private void activeSession() {
        when(gameSessionService.getActiveSession())
                .thenReturn(Optional.of(GameSession.builder()
                        .sessionId(7L).campaign(campaign).build()));
    }

    private CharacterSheet characterSheet() {
        return CharacterSheet.builder()
                .sheetId(SHEET_ID)
                .owner(owner)
                .campaign(campaign)
                .player(player)
                .name("Luigi")
                .build();
    }

    private Token token(int posX, int posY) {
        return Token.builder()
                .tokenId(TOKEN_ID)
                .sheet(characterSheet())
                .type(TokenType.PC)
                .posX(posX)
                .posY(posY)
                .build();
    }
}