package com.marcomoretta.dungeondesk.exception;

/**
 * Thrown when the requester tries to get an unknown player
 */
public class PlayerNotFoundException extends ResourceNotFoundException {
    public PlayerNotFoundException(String message) {
        super(message);
    }
}
