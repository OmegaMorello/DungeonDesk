package com.marcomoretta.dungeondesk.domain.request;

/**
 * The request to update an existing app user in the service layer. To be used through a dto
 *
 * @param appUserId     The id of the app user to update
 * @param username      The updated name
 * @param currentSecret The current secret
 * @param newSecret     The updated secret
 */
public record UpdateAppUserRequest(
        Long appUserId,
        String username,
        String currentSecret,
        String newSecret
) {
}
