package com.marcomoretta.dungeondesk.exception;

public class DuplicateCampaignPerUserException extends DuplicateResourceException {
    public DuplicateCampaignPerUserException(String message) {
        super(message);
    }
}
