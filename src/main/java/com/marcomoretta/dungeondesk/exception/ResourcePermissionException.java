package com.marcomoretta.dungeondesk.exception;

public abstract class ResourcePermissionException extends RuntimeException {
    public ResourcePermissionException(String message) {
        super(message);
    }
}
