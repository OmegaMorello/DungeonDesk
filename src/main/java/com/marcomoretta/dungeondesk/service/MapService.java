package com.marcomoretta.dungeondesk.service;

import com.marcomoretta.dungeondesk.auth.AuthSession;
import com.marcomoretta.dungeondesk.domain.dto.MapImageDto;
import com.marcomoretta.dungeondesk.domain.entity.MapState;
import com.marcomoretta.dungeondesk.domain.entity.Token;
import com.marcomoretta.dungeondesk.domain.entity.TokenType;
import org.springframework.web.multipart.MultipartFile;

/**
 * Interface for the map service
 */
public interface MapService {

    /**
     * Gets the map of the current campaign
     *
     * @param authSession The actual session
     * @return The map and its tokens
     */
    MapState getMap(AuthSession authSession);

    /**
     * If the map is empty, it creates one, else it resizes the existing map
     *
     * @param campaignId  The id of the campaign the map belongs to
     * @param gridRows    The number of rows
     * @param gridColumns The number of columns
     * @param authSession The actual session
     * @return The created or resized map
     */
    MapState createOrResizeMap(Long campaignId, int gridRows, int gridColumns, AuthSession authSession);

    /**
     * Creates a token out of a sheet and places it on the map
     *
     * @param sheetId     The id of the sheet the token belongs to
     * @param tokenType   The type of token (usually PC or NPC)
     * @param posX        The X position on the grid
     * @param posY        The Y position on the grid
     * @param authSession The actual session
     * @return The added token
     */
    Token addToken(Long sheetId, TokenType tokenType, int posX, int posY, AuthSession authSession);

    /**
     * Moves a token on the grid
     *
     * @param tokenId     The id of the token to be moved
     * @param posX        The X position on the grid
     * @param posY        The Y position on the grid
     * @param authSession The actual session
     * @return The moved Token
     */
    Token moveToken(Long tokenId, int posX, int posY, AuthSession authSession);

    /**
     * Removes a token from the map
     *
     * @param tokenId     The id of the token to remove
     * @param authSession The actual session
     */
    void deleteToken(Long tokenId, AuthSession authSession);

    /**
     * Stores the map background locally, replaces the previous one if any
     *
     * @param file        The map file to be saved
     * @param authSession The actual session
     */
    void storeBackground(MultipartFile file, AuthSession authSession);

    /**
     * Reads the map background to be shown on screen
     *
     * @param authSession The actual session
     * @return The dto containing the map bytes and file type
     */
    MapImageDto readBackground(AuthSession authSession);
}
