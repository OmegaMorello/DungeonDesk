package com.marcomoretta.dungeondesk.domain.dto;

import java.util.List;

/**
 * Dto to expose the map and its tokens
 *
 * @param mapStateId    The id of the map state
 * @param gridRows      The number of rows
 * @param gridColumns   The number of columns
 * @param hasBackground False if no image has yet been loaded
 * @param tokenList     The list of the tokens placed on the map
 */
public record MapStateDto(
        Long mapStateId,
        int gridRows,
        int gridColumns,
        boolean hasBackground,
        List<TokenDto> tokenList
) {
}
