package com.marcomoretta.dungeondesk.domain.request;

/**
 * The request to change the join code of a session in the service layer.
 * To be used through a dto.
 *
 * @param sessionId The id of the session to update
 * @param joinCode  The updated join code
 */
public record UpdateGameSessionRequest(
        Long sessionId,
        String joinCode
) {
}
