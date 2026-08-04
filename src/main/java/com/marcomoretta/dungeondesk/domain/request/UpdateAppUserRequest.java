package com.marcomoretta.dungeondesk.domain.request;

/**
 * The request to update an existing campaign in the service layer. To be used through a dto
 *
 * @param id       The id of the campaign to update
 * @param username The updated name
 * @param secret   The updated secret
 */
public record UpdateAppUserRequest(
        Long id,
        String username,
        String secret
) {
}
