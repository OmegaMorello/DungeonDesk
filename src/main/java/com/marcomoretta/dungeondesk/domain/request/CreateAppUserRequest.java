package com.marcomoretta.dungeondesk.domain.request;

/**
 * The request to create a new user in the service layer. To be used through a dto
 *
 * @param username The username
 * @param secret   The raw secret
 */
public record CreateAppUserRequest(
        String username,
        String secret
) {
}
