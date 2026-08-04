package com.marcomoretta.dungeondesk.exception;

public class UnauthenticatedException extends RuntimeException {
    public UnauthenticatedException() {
        super("Unable to find an authentication session token");
    }
}
