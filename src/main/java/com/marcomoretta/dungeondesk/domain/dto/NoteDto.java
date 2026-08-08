package com.marcomoretta.dungeondesk.domain.dto;

import java.time.Instant;

/**
 * Dto to expose fields of Note
 *
 * @param noteId            The id of the note
 * @param campaignId        The campaign the note belongs to, always present
 * @param sessionId         The game session the note belongs to, null for general campaign notes
 * @param sharedWithPlayers False: visible only to the Dungeon Master. True: visible to everyone
 * @param text              The note content
 * @param created           Creation timestamp
 */
public record NoteDto(
        Long noteId,
        Long campaignId,
        Long sessionId,
        boolean sharedWithPlayers,
        String text,
        Instant created
) {
}
