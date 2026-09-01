package com.marcomoretta.dungeondesk.exception;

/**
 * Thrown when the requester tries to get an unknown user
 */
public class AppUserNotFoundException extends ResourceNotFoundException {
    public AppUserNotFoundException(String message) {
        super(message);
    }
}
