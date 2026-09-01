package com.marcomoretta.dungeondesk.exception;

/**
 * Thrown when the requester tries to add a player name that already exists
 */
public class DuplicatePlayerException extends DuplicateResourceException {
    public DuplicatePlayerException(String message) {
        super(message);
    }
}
