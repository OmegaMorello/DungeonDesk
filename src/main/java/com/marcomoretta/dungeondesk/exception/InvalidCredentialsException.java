package com.marcomoretta.dungeondesk.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Invalid credentials"); // No telling if the user or password was wrong for safety reasons
    }
}
