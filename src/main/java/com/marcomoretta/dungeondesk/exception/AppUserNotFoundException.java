package com.marcomoretta.dungeondesk.exception;

public class AppUserNotFoundException extends ResourceNotFoundException {
    public AppUserNotFoundException(String message) {
        super(message);
    }
}
