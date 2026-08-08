package com.marcomoretta.dungeondesk.domain.dto;

/**
 * Dto exposed on the public login screen: the roster of the running session.
 * Carries the display name only
 *
 * @param name The player name as wrote down by the Dungeon Master
 */
public record SessionPlayerDto(
        String name
) {
}
