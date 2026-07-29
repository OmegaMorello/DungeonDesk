package com.marcomoretta.dungeondesk.domain.dto;

/**
 * Dto to expose not-sensitive fields of AppUser
 *
 * @param id           The id of the user
 * @param username     The name of the user
 */
public record AppUserDto(
        Long id,
        String username
) {
}
