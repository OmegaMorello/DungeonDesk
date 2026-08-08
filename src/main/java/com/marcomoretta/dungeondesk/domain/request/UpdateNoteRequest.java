package com.marcomoretta.dungeondesk.domain.request;

/**
 * The request to update an existing note in the service layer. To be used through a dto.
 *
 * @param noteId            The id of the note to update
 * @param sharedWithPlayers The updated visibility
 * @param text              The updated content
 */
public record UpdateNoteRequest(
        Long noteId,
        boolean sharedWithPlayers,
        String text
) {
}
