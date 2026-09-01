package com.marcomoretta.dungeondesk.exception;

/**
 * Thrown when the requester tries to get an unknown note
 */
public class NoteNotFoundException extends ResourceNotFoundException {
    public NoteNotFoundException(String message) {
        super(message);
    }
}
