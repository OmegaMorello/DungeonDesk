package com.marcomoretta.dungeondesk.exception;

/**
 * Thrown when a note does not exist
 */
public class NoteNotFoundException extends ResourceNotFoundException {
    public NoteNotFoundException(String message) {
        super(message);
    }
}
