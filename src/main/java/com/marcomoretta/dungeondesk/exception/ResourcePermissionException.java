package com.marcomoretta.dungeondesk.exception;

/**
 * Abstract class to help the global exception handler
 * Extend when an unauthorized exception is needed
 */
public class ResourcePermissionException extends RuntimeException {
    public ResourcePermissionException(String message) {
        super(message);
    }
}
