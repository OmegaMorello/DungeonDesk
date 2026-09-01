package com.marcomoretta.dungeondesk.exception;

/**
 * Thrown when the requester tries to login with wrong user or password
 */
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Invalid credentials"); // No telling if the user or password was wrong for safety reasons
    }
}
