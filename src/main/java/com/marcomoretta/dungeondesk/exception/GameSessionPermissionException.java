package com.marcomoretta.dungeondesk.exception;

/**
 * Thrown when the requester does not own the campaign the session belongs to
 */
public class GameSessionPermissionException extends ResourcePermissionException {
    public GameSessionPermissionException(String message) {
        super(message);
    }
}
