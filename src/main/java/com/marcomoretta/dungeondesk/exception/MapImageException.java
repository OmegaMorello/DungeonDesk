package com.marcomoretta.dungeondesk.exception;

/**
 * Thrown when the requester tries to upload a not allowed file
 */
public class MapImageException extends RuntimeException {
    public MapImageException(String message) {
        super(message);
    }
}
