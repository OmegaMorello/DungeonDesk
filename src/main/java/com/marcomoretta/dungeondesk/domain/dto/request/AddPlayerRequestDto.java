package com.marcomoretta.dungeondesk.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Dto needed to pass Player addition fields
 *
 * @param name The name of the player
 */
public record AddPlayerRequestDto(

        @NotBlank(message = ERROR_MESSAGE_NAME_LENGTH)
        @Size(max = 255, message = ERROR_MESSAGE_NAME_LENGTH)
        String name
) {
    /**
     * Trims (strips) the name during construction
     */
    public AddPlayerRequestDto {
        if (name != null) name = name.strip();
    }

    private static final String ERROR_MESSAGE_NAME_LENGTH = "Name must be between 1 and 255 characters long";
}
