package com.marcomoretta.dungeondesk.service.impl;

import com.marcomoretta.dungeondesk.auth.AuthSession;
import com.marcomoretta.dungeondesk.auth.LoginType;
import com.marcomoretta.dungeondesk.domain.dto.MapImageDto;
import com.marcomoretta.dungeondesk.domain.entity.*;
import com.marcomoretta.dungeondesk.exception.*;
import com.marcomoretta.dungeondesk.repository.CampaignRepository;
import com.marcomoretta.dungeondesk.repository.MapStateRepository;
import com.marcomoretta.dungeondesk.repository.SheetRepository;
import com.marcomoretta.dungeondesk.repository.TokenRepository;
import com.marcomoretta.dungeondesk.service.CampaignService;
import com.marcomoretta.dungeondesk.service.GameSessionService;
import com.marcomoretta.dungeondesk.service.MapService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

/**
 * Serves the map controller
 */
@Service
public class MapServiceImpl implements MapService {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg",
            "image/png"
    );

    private final MapStateRepository mapStateRepository;
    private final TokenRepository tokenRepository;
    private final SheetRepository sheetRepository;
    private final CampaignService campaignService;
    private final GameSessionService gameSessionService;
    private final Path imageDirectory;

    public MapServiceImpl(MapStateRepository mapStateRepository,
                          TokenRepository tokenRepository,
                          SheetRepository sheetRepository,
                          CampaignRepository campaignRepository, CampaignService campaignService,
                          GameSessionService gameSessionService,
                          @Value("${dungeondesk.map-images-dir}") String imageDirectory) {
        this.mapStateRepository = mapStateRepository;
        this.tokenRepository = tokenRepository;
        this.sheetRepository = sheetRepository;
        this.campaignService = campaignService;
        this.gameSessionService = gameSessionService;
        this.imageDirectory = Path.of(imageDirectory);
    }


    @Override
    @Transactional(readOnly = true)
    public MapState getMap(AuthSession authSession) {
        return findMap(authSession);
    }

    @Override
    @Transactional
    public MapState createOrResizeMap(Long campaignId, int gridRows, int gridColumns, AuthSession authSession) {

        // Get the map, if it doesn't exist create a new one
        MapState mapState = mapStateRepository.findByCampaign_CampaignId(campaignId)
                .orElseGet(() -> MapState.builder()
                        .campaign(campaignService.getCampaign(campaignId))
                        .build());

        mapState.setGridRows(gridRows);
        mapState.setGridColumns(gridColumns);

        for (Token token : mapState.getTokenList()) {
            token.setPosX(Math.min(token.getPosX(), gridColumns));
            token.setPosY(Math.min(token.getPosY(), gridRows));
        }

        return mapStateRepository.save(mapState);
    }

    @Override
    @Transactional
    public Token addToken(Long sheetId, TokenType tokenType, int posX, int posY, AuthSession authSession) {

        MapState mapState = findMap(authSession);
        checkPosition(mapState, posX, posY);

        GenericSheet sheet = sheetRepository.findById(sheetId)
                .orElseThrow(() -> new SheetNotFoundException("Sheet not found: " + sheetId));

        if (tokenRepository.existsBySheet_SheetId(sheetId))
            throw new TokenPermissionException("Creature already on the map");

        Token token = Token.builder()
                .sheet(sheet)
                .type(tokenType)
                .posX(posX)
                .posY(posY)
                .build();

        mapState.addToken(token);
        mapStateRepository.save(mapState);

        return token;
    }

    @Override
    @Transactional
    public Token moveToken(Long tokenId, int posX, int posY, AuthSession authSession) {

        Token token = findToken(tokenId);
        checkPosition(token.getMapState(), posX, posY);
        checkAuth(token, authSession);

        token.setPosX(posX);
        token.setPosY(posY);

        return token;
    }

    @Override
    @Transactional
    public void deleteToken(Long tokenId, AuthSession authSession) {
        Token token = findToken(tokenId);
        checkAuth(token, authSession);
        token.getMapState().removeToken(token);
        tokenRepository.delete(token);
    }

    @Override
    @Transactional
    public void storeBackground(MultipartFile file, AuthSession authSession) {
        if (file.isEmpty() || !ALLOWED_TYPES.contains(file.getContentType()))
            throw new MapImageException("Only PNG and JPEG images are allowed");

        MapState mapState = findMap(authSession);
        String fileName = createMapFileName(mapState);

        try {
            Files.createDirectories(imageDirectory);
            Files.write(imageDirectory.resolve(fileName), file.getBytes());
        } catch (IOException exception) {
            throw new MapImageException("Cannot store map image");
        }

        mapState.setBackgroundUrl(fileName);
        mapState.setBackgroundContentType(file.getContentType());
    }

    @Override
    public MapImageDto readBackground(AuthSession authSession) {
        MapState mapState = findMap(authSession);

        if (mapState.getBackgroundUrl() == null || mapState.getBackgroundUrl().isBlank())
            throw new MapStateNotFoundException("No background found");

        try {
            return new MapImageDto(
                    Files.readAllBytes(imageDirectory.resolve(mapState.getBackgroundUrl())),
                    mapState.getBackgroundContentType());
        } catch (IOException exception) {
            throw new MapStateNotFoundException("Map image not found");
        }
    }

    // If the requester is a DM, the campaign is taken from the session
    private Long getCampaignId(AuthSession authSession) {
        return authSession.loginType() == LoginType.MASTER
                ? gameSessionService.getActiveSession().map(
                        gameSession -> gameSession.getCampaign().getCampaignId())
                .orElseThrow(() -> new MapStateNotFoundException("No map state found"))
                : authSession.campaignId();
    }

    private MapState findMap(AuthSession authSession) {
        return mapStateRepository.findByCampaign_CampaignId(getCampaignId(authSession))
                .orElseThrow(() -> new MapStateNotFoundException("No map was found for this campaign"));
    }

    private void checkPosition(MapState mapState, int posX, int posY) {
        if (posX > mapState.getGridColumns() || posY > mapState.getGridRows() || posX < 1 || posY < 1)
            throw new TokenPermissionException("Token must be inside the map");
    }

    // The DM can move any token, a player only its own
    private void checkAuth(Token token, AuthSession authSession) {
        if (authSession.loginType().equals(LoginType.MASTER)) return; // Master can always move

        if (token.getSheet() instanceof CharacterSheet sheet
                && sheet.getPlayer() != null
                && sheet.getPlayer().getPlayerId().equals(authSession.playerId())) return;

        throw new TokenPermissionException("You can only act on your own token");
    }

    private Token findToken(Long tokenId) {
        return tokenRepository.findById(tokenId)
                .orElseThrow(() -> new TokenNotFoundException("Token not found: " + tokenId));
    }

    // Creates a custom file name using the map state id
    private String createMapFileName(MapState mapState) {
        return "map-" + mapState.getMapStateId() + ".img";
    }
}
