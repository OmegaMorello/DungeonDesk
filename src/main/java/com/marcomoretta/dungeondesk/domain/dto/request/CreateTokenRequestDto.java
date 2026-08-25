package com.marcomoretta.dungeondesk.domain.dto.request;

import com.marcomoretta.dungeondesk.domain.entity.TokenType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Dto to create a token and place it on the map
 *
 * @param sheetId The sheet the token belongs to
 * @param tokenType The type of token
 * @param posX The X position on the map
 * @param posY The Y position on the map
 */
public record CreateTokenRequestDto(
        @NotNull
        Long sheetId,

        @NotNull
        TokenType tokenType,

        @Min(1)
        int posX,

        @Min(1)
        int posY
) {
}
