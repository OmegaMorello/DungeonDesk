package com.marcomoretta.dungeondesk.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Dto needed to pass Campaign creation fields
 *
 * @param name   The name of the campaign
 * @param ownerId The id of the user who creates the campaign
 * @param description The optional description of the campaign
 */
public record CreateCampaignRequestDto(
        @NotBlank(message = ERROR_MESSAGE_NAME_LENGTH)
        @Size(max = 255, message = ERROR_MESSAGE_NAME_LENGTH)
        String name,

        @NotNull(message = ERROR_MESSAGE_OWNER_EMPTY)
        Long ownerId,

        @Size(max = 1000, message = ERROR_MESSAGE_DESCRIPTION_LENGTH)
        String description
) {
    /**
     * Trims (strips) the name and the description during construction
     */
    public CreateCampaignRequestDto {
        if (name != null) name = name.strip();
        if (description != null) description = description.strip();
    }

    private static final String ERROR_MESSAGE_NAME_LENGTH = "Name must be between 1 and 255 characters long";
    private static final String ERROR_MESSAGE_OWNER_EMPTY = "You must be logged in to add a campaign";
    private static final String ERROR_MESSAGE_DESCRIPTION_LENGTH = "Description maximum length is 1000 characters";
}
