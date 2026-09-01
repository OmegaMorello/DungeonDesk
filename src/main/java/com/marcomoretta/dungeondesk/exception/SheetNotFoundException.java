package com.marcomoretta.dungeondesk.exception;

/**
 * Thrown when the requester tries to get an unknown sheet
 */
public class SheetNotFoundException extends ResourceNotFoundException {
    public SheetNotFoundException(String message) {
        super(message);
    }
}
