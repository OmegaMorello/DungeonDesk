package com.marcomoretta.dungeondesk.exception;

/**
 * Thrown when the requester tries to get an unknown game session
 */
public class GameSessionNotFoundException extends ResourceNotFoundException {
    public GameSessionNotFoundException(String message) {
        super(message);
    }
}
