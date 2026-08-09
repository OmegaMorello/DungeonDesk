package com.marcomoretta.dungeondesk.exception;

/**
 * Thrown when the requester may not read or write a sheet
 */
public class SheetPermissionException extends ResourcePermissionException {
    public SheetPermissionException(String message) {
        super(message);
    }
}
