package com.marcomoretta.dungeondesk.domain.dto;

/**
 * Dto to expose fields of Player
 *
 * @param id   The id of the player
 * @param name The name of the player
 */
public record PlayerDto(
        Long id,
        String name
) {
}
