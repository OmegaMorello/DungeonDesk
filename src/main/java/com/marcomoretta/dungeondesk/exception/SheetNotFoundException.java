package com.marcomoretta.dungeondesk.exception;

/**
 * Thrown when a sheet does not exist
 */
public class SheetNotFoundException extends ResourceNotFoundException {
    public SheetNotFoundException(String message) {
        super(message);
    }
}
