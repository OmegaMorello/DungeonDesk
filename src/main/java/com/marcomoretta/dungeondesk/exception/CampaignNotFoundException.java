package com.marcomoretta.dungeondesk.exception;

public class CampaignNotFoundException extends ResourceNotFoundException {
    public CampaignNotFoundException(String message) {
        super(message);
    }
}
