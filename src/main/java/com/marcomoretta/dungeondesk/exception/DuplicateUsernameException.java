package com.marcomoretta.dungeondesk.exception;

public class DuplicateUsernameException extends DuplicateResourceException {
    public DuplicateUsernameException(String message) {
        super(message);
    }
}
