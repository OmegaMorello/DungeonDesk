package com.marcomoretta.dungeondesk.exception;

/**
 * Abstract class to help the global exception handler
 */
public abstract class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
