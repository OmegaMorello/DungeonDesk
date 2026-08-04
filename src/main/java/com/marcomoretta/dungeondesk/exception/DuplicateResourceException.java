package com.marcomoretta.dungeondesk.exception;

/**
 * Abstract class to help the global exception handler
 * Extend when a duplicate field exception is needed
 */
public abstract class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
