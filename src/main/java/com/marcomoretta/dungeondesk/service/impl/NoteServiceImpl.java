package com.marcomoretta.dungeondesk.service.impl;

import com.marcomoretta.dungeondesk.auth.AuthSession;
import com.marcomoretta.dungeondesk.auth.LoginType;
import com.marcomoretta.dungeondesk.domain.entity.Campaign;
import com.marcomoretta.dungeondesk.domain.entity.GameSession;
import com.marcomoretta.dungeondesk.domain.entity.Note;
import com.marcomoretta.dungeondesk.domain.request.CreateNoteRequest;
import com.marcomoretta.dungeondesk.domain.request.UpdateNoteRequest;
import com.marcomoretta.dungeondesk.exception.GameSessionNotFoundException;
import com.marcomoretta.dungeondesk.exception.NoteNotFoundException;
import com.marcomoretta.dungeondesk.exception.NotePermissionException;
import com.marcomoretta.dungeondesk.repository.GameSessionRepository;
import com.marcomoretta.dungeondesk.repository.NoteRepository;
import com.marcomoretta.dungeondesk.service.CampaignService;
import com.marcomoretta.dungeondesk.service.NoteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serves the Note controller.
 */
@Service
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final GameSessionRepository gameSessionRepository;
    private final CampaignService campaignService;

    public NoteServiceImpl(NoteRepository noteRepository,
                           GameSessionRepository gameSessionRepository,
                           CampaignService campaignService) {
        this.noteRepository = noteRepository;
        this.gameSessionRepository = gameSessionRepository;
        this.campaignService = campaignService;
    }

    @Override
    @Transactional
    public Note createNote(CreateNoteRequest request, AuthSession session) {

        Campaign campaign = requireOwnedCampaign(request.campaignId(), session);

        GameSession gameSession = null;
        if (request.gameSessionId() != null) {
            gameSession = gameSessionRepository.findById(request.gameSessionId())
                    .orElseThrow(() -> new GameSessionNotFoundException(
                            "Session not found: " + request.gameSessionId()));

            // Prevent attaching a note to a session of a different campaign
            if (!gameSession.getCampaign().getCampaignId().equals(campaign.getCampaignId()))
                throw new NotePermissionException(
                        "The session does not belong to the given campaign");
        }

        Note note = Note.builder()
                .campaign(campaign)
                .gameSession(gameSession)
                .sharedWithPlayers(request.sharedWithPlayers())
                .text(request.text())
                .build();

        return noteRepository.save(note);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Note> getCampaignNotes(Long campaignId, AuthSession session) {

        if (isMaster(session)) {
            requireOwnedCampaign(campaignId, session);
            return noteRepository.findByCampaign_CampaignIdOrderByCreatedAsc(campaignId);
        }

        requireOwnCampaignAsPlayer(campaignId, session);
        return noteRepository.findByCampaign_CampaignIdAndSharedWithPlayersTrueOrderByCreatedAsc(campaignId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Note> getSessionNotes(Long gameSessionId, AuthSession session) {

        GameSession gameSession = gameSessionRepository.findById(gameSessionId)
                .orElseThrow(() -> new GameSessionNotFoundException("Session not found: " + gameSessionId));

        Long campaignId = gameSession.getCampaign().getCampaignId();

        if (isMaster(session)) {
            requireOwnedCampaign(campaignId, session);
            return noteRepository.findByGameSession_SessionIdOrderByCreatedAsc(gameSessionId);
        }

        requireOwnCampaignAsPlayer(campaignId, session);
        return noteRepository.findByGameSession_SessionIdAndSharedWithPlayersTrueOrderByCreatedAsc(gameSessionId);
    }

    @Override
    @Transactional
    public Note updateNote(UpdateNoteRequest request, AuthSession session) {

        Note note = getNote(request.noteId());
        requireOwnedCampaign(note.getCampaign().getCampaignId(), session);

        note.setText(request.text());
        note.setSharedWithPlayers(request.sharedWithPlayers());

        return noteRepository.save(note);
    }

    @Override
    @Transactional
    public void deleteNote(Long noteId, AuthSession session) {

        Note note = getNote(noteId);
        requireOwnedCampaign(note.getCampaign().getCampaignId(), session);

        noteRepository.delete(note);
    }

    private Note getNote(Long noteId) {
        return noteRepository.findById(noteId)
                .orElseThrow(() -> new NoteNotFoundException("Note not found: " + noteId));
    }

    private boolean isMaster(AuthSession session) {
        return session.loginType() == LoginType.MASTER;
    }

    /**
     * Loads a campaign making sure the caller is the master who owns it.
     */
    private Campaign requireOwnedCampaign(Long campaignId, AuthSession session) {
        session.requireMaster();

        Campaign campaign = campaignService.getCampaign(campaignId);

        if (!campaign.getOwner().getUserId().equals(session.userId()))
            throw new NotePermissionException("Only the owner of the campaign can manage its notes");

        return campaign;
    }

    /**
     * A player is bound to the campaign recorded in their session
     */
    private void requireOwnCampaignAsPlayer(Long campaignId, AuthSession session) {
        if (!campaignId.equals(session.campaignId()))
            throw new NotePermissionException("You can only read the notes of your own campaign");
    }
}
