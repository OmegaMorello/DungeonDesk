package com.marcomoretta.dungeondesk.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Dto needed to pass Campaign update fields
 *
 * @param name        The updated name
 * @param description The updated description
 */
public record UpdateCampaignRequestDto(
        @NotBlank(message = ERROR_MESSAGE_NAME_LENGTH)
        @Size(max = 255, message = ERROR_MESSAGE_NAME_LENGTH)
        String name,

        @Size(max = 1000, message = ERROR_MESSAGE_DESCRIPTION_LENGTH)
        String description
) {
    /**
     * Trims (strips) the name and the description during construction
     */
    public UpdateCampaignRequestDto {
        if (name != null) name = name.strip();
        if (description != null) description = description.strip();
    }

    private static final String ERROR_MESSAGE_CAMPAIGN_EMPTY = "You must specify the campaign to update";
    private static final String ERROR_MESSAGE_NAME_LENGTH = "Name must be between 1 and 255 characters long";
    private static final String ERROR_MESSAGE_DESCRIPTION_LENGTH = "Description maximum length is 1000 characters";
}
