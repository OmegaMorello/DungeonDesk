package com.marcomoretta.dungeondesk.exception;

/**
 * Thrown when the requester tries to do something they are not authenticated for
 */
public class UnauthenticatedException extends RuntimeException {
    public UnauthenticatedException() {
        super("Unable to find an authentication session token");
    }
}
