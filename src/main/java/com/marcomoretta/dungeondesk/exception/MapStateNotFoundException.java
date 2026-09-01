package com.marcomoretta.dungeondesk.exception;

/**
 * Thrown when the requester tries to get an unknown map state
 */
public class MapStateNotFoundException extends ResourceNotFoundException {
    public MapStateNotFoundException(String message) {
        super(message);
    }
}
