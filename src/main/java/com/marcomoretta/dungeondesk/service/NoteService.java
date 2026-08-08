package com.marcomoretta.dungeondesk.service;

import com.marcomoretta.dungeondesk.auth.AuthSession;
import com.marcomoretta.dungeondesk.domain.entity.Note;
import com.marcomoretta.dungeondesk.domain.request.CreateNoteRequest;
import com.marcomoretta.dungeondesk.domain.request.UpdateNoteRequest;

import java.util.List;

/**
 * Interface that defines the Note Service.
 *
 * Every method takes the caller's AuthSession rather than a plain id: reading
 * notes requires both the identity and the role, because a player may only see the
 * notes flagged as shared. Writing is reserved to the Dungeon Master who owns the
 * campaign.
 */
public interface NoteService {

    /**
     * Creates a note on a campaign, optionally bound to one of its sessions
     *
     * @param request The note creation request details
     * @param session The caller session, must be a master owning the campaign
     * @return The newly created note
     */
    Note createNote(CreateNoteRequest request, AuthSession session);

    /**
     * Gets the notes of a campaign, filtered by what the caller is allowed to read
     *
     * @param campaignId The campaign to read
     * @param session    The caller session
     * @return All the notes for a master, only the shared ones for a player
     */
    List<Note> getCampaignNotes(Long campaignId, AuthSession session);

    /**
     * Gets the notes bound to a specific game session, filtered by what the caller is allowed to read
     *
     * @param gameSessionId The session to read
     * @param session       The caller session
     * @return All the notes for a master, only the shared ones for a player
     */
    List<Note> getSessionNotes(Long gameSessionId, AuthSession session);

    /**
     * Updates the content or the visibility of a note
     *
     * @param request The note update request details
     * @param session The caller session, must be a master owning the campaign
     * @return The updated note
     */
    Note updateNote(UpdateNoteRequest request, AuthSession session);

    /**
     * Deletes a note
     *
     * @param noteId  The id of the note to delete
     * @param session The caller session, must be a master owning the campaign
     */
    void deleteNote(Long noteId, AuthSession session);
}
