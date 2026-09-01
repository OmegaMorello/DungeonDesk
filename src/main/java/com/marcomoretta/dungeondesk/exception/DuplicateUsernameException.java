package com.marcomoretta.dungeondesk.exception;

/**
 * Thrown when the requester tries to register with an already taken username
 */
public class DuplicateUsernameException extends DuplicateResourceException {
    public DuplicateUsernameException(String message) {
        super(message);
    }
}
