package com.marcomoretta.dungeondesk.exception;

/**
 * Thrown when the requester tries to open a session while another one is still running.
 * The application allows a single open session at a time.
 */
public class SessionAlreadyOpenException extends DuplicateResourceException {
    public SessionAlreadyOpenException(String message) {
        super(message);
    }
}
