package com.marcomoretta.dungeondesk.exception;

/**
 * Thrown when the requester tries to create a campaign with a name that already exists with the same owner
 */
public class DuplicateCampaignPerUserException extends DuplicateResourceException {
    public DuplicateCampaignPerUserException(String message) {
        super(message);
    }
}
