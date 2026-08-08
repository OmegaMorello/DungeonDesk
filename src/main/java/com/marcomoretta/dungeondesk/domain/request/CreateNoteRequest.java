package com.marcomoretta.dungeondesk.domain.request;

/**
 * The request to create a note in the service layer. To be used through a dto.
 *
 * @param campaignId        The campaign the note belongs to
 * @param gameSessionId     Optional session the note belongs to, null for a general campaign note
 * @param sharedWithPlayers Whether the players may read the note
 * @param text              The note content
 */
public record CreateNoteRequest(
        Long campaignId,
        Long gameSessionId,
        boolean sharedWithPlayers,
        String text
) {
}
