package com.marcomoretta.dungeondesk.domain.dto;

/**
 * Dto to expose not-sensitive fields of AppUser
 *
 * @param appUserId The id of the user
 * @param username  The name of the user
 */
public record AppUserDto(
        Long appUserId,
        String username
) {
}
