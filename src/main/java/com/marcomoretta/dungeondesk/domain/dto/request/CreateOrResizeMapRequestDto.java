package com.marcomoretta.dungeondesk.domain.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Dto to create or resize the campaign map
 *
 * @param campaignId  The campaign id the map belongs to
 * @param gridRows    The number of rows
 * @param gridColumns The number of columns
 */
public record CreateOrResizeMapRequestDto(
        @NotNull
        Long campaignId,

        @Min(1)
        @Max(50)
        int gridRows,

        @Min(1)
        @Max(50)
        int gridColumns
) {
}
