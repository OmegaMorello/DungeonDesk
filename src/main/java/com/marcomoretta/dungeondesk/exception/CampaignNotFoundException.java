package com.marcomoretta.dungeondesk.exception;

/**
 * Thrown when the requester tries to get an unknown campaign
 */
public class CampaignNotFoundException extends ResourceNotFoundException {
    public CampaignNotFoundException(String message) {
        super(message);
    }
}
