package com.marcomoretta.dungeondesk.exception;

/**
 * Thrown when the requester does not have the permission to get a campaign
 */
public class CampaignPermissionException extends ResourcePermissionException {
    public CampaignPermissionException(String message) {
        super(message);
    }
}
