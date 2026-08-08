package com.marcomoretta.dungeondesk.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Dto needed to open a new game session.
 *
 * @param joinCode The code the Dungeon Master chooses and communicates to the players
 */
public record CreateGameSessionRequestDto(
        @NotBlank(message = EMPTY_JOIN_CODE)
        @Size(min = 4, max = 32, message = JOIN_CODE_LENGTH)
        String joinCode
) {
    /**
     * Trims (strips) the join code during construction
     */
    public CreateGameSessionRequestDto {
        if (joinCode != null) joinCode = joinCode.strip();
    }

    private static final String EMPTY_JOIN_CODE = "The join code cannot be empty";
    private static final String JOIN_CODE_LENGTH = "The join code must be between 4 and 32 characters long";
}
