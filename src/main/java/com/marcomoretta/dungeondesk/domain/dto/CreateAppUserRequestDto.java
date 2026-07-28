package com.marcomoretta.dungeondesk.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Dto needed to pass AppUser creation or login parameters
 * @param name the username of the user
 * @param secret the raw secret which will be hashed before being saved in the db
 */
public record CreateAppUserRequestDto(
        @NotBlank(message = ERROR_MESSAGE_NAME_LENGTH)
        @Size(max = 255, message = ERROR_MESSAGE_NAME_LENGTH)
        String name,
        @NotBlank(message = ERROR_MESSAGE_SECRET_LENGTH)
        @Size(min = 8, message = ERROR_MESSAGE_SECRET_LENGTH)
        String secret
) {
    private static final String ERROR_MESSAGE_NAME_LENGTH = "Name must be between 1 and 255 characters long";
    private static final String ERROR_MESSAGE_SECRET_LENGTH = "Secret must be at least 8 characters long";
}
