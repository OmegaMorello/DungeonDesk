package com.marcomoretta.dungeondesk.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Dto needed to pass App User update fields
 *
 * @param username      The updated username
 * @param currentSecret The current secret
 * @param newSecret     The updated secret
 */
public record UpdateAppUserRequestDto(
        @NotBlank(message = ERROR_MESSAGE_NAME_LENGTH)
        @Size(max = 255, message = ERROR_MESSAGE_NAME_LENGTH)
        String username,

        @NotBlank
        String currentSecret,

        @NotBlank(message = ERROR_MESSAGE_SECRET_LENGTH)
        @Size(min = 8, message = ERROR_MESSAGE_SECRET_LENGTH)
        String newSecret
) {
    /**
     * Trims (strips) the name during construction
     */
    public UpdateAppUserRequestDto {
        if (username != null) username = username.strip();
    }

    private static final String ERROR_MESSAGE_NAME_LENGTH = "Name must be between 1 and 255 characters long";
    private static final String ERROR_MESSAGE_SECRET_LENGTH = "Secret must be at least 8 characters long";
}
