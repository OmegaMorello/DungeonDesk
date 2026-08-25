package com.marcomoretta.dungeondesk.exception;

public class TokenNotFoundException extends ResourceNotFoundException {
    public TokenNotFoundException(String message) {
        super(message);
    }
}
