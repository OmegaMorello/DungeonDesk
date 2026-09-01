package com.marcomoretta.dungeondesk.exception;

/**
 * Thrown when the requester may not add or move a sheet
 */
public class TokenPermissionException extends ResourcePermissionException {
    public TokenPermissionException(String message) {
        super(message);
    }
}
