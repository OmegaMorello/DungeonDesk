package com.marcomoretta.dungeondesk.exception;

/**
 * Thrown when a game session does not exist
 */
public class GameSessionNotFoundException extends ResourceNotFoundException {
    public GameSessionNotFoundException(String message) {
        super(message);
    }
}
