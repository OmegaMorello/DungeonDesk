package com.marcomoretta.dungeondesk.domain.request;

/**
 * The request to create a new user in the service layer. To be used through a dto
 * @param name username
 * @param secret raw secret
 */
public record CreateAppUserRequest(
        String name,
        String secret
) {
}
