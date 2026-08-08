package com.marcomoretta.dungeondesk.exception;

/**
 * Thrown when the requester may not read or write a given note.
 */
public class NotePermissionException extends ResourcePermissionException {
    public NotePermissionException(String message) {
        super(message);
    }
}
