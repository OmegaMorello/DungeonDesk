package com.marcomoretta.dungeondesk.domain.dto.request;

import jakarta.validation.constraints.Min;

/**
 * Dto to move an existing token on the map
 *
 * @param posX The X position on the map
 * @param posY The Y position on the map
 */
public record MoveTokenRequestDto(
        @Min(1)
        int posX,

        @Min(1)
        int posY
) {
}
