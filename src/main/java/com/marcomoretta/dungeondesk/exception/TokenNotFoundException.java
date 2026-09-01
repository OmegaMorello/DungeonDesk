package com.marcomoretta.dungeondesk.exception;

/**
 * Thrown when the requester tries to get an unknown token
 */
public class TokenNotFoundException extends ResourceNotFoundException {
    public TokenNotFoundException(String message) {
        super(message);
    }
}
