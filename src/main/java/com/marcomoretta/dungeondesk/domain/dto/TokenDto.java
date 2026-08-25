package com.marcomoretta.dungeondesk.domain.dto;

import com.marcomoretta.dungeondesk.domain.entity.TokenType;

/**
 * Dto to expose a token on the map
 *
 * @param tokenId   The id of the token
 * @param sheetId   The sheet id the token belongs to
 * @param sheetName The name of the sheet the token belongs to. Saved here to avoid 1 call per token just to get the sheet name
 * @param tokenType The type of token (usually PC or NPC)
 * @param posX      The X position on the map
 * @param posY      The Y position on the map
 */
public record TokenDto(
        Long tokenId,
        Long sheetId,
        String sheetName,
        TokenType tokenType,
        int posX,
        int posY
) {
}
