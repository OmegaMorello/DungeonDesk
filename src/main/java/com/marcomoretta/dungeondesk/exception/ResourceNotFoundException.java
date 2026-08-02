package com.marcomoretta.dungeondesk.exception;

/**
 * Abstract class to help the global exception handler
 */
public abstract class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
