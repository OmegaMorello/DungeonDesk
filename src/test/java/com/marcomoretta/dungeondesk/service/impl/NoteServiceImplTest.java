package com.marcomoretta.dungeondesk.service.impl;

import com.marcomoretta.dungeondesk.auth.AuthSession;
import com.marcomoretta.dungeondesk.auth.LoginType;
import com.marcomoretta.dungeondesk.domain.entity.*;
import com.marcomoretta.dungeondesk.domain.request.CreateNoteRequest;
import com.marcomoretta.dungeondesk.domain.request.UpdateNoteRequest;
import com.marcomoretta.dungeondesk.exception.*;
import com.marcomoretta.dungeondesk.repository.GameSessionRepository;
import com.marcomoretta.dungeondesk.repository.NoteRepository;
import com.marcomoretta.dungeondesk.service.CampaignService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoteServiceImplTest {

    // Using fixed ids for ease of use
    private final static Long OWNER_ID = 1L;
    private final static Long CAMPAIGN_ID = 2L;
    private final static Long SESSION_ID = 3L;
    private final static Long NOTE_ID = 4L;
    private final static Long OTHER_ID = 99L;

    @Mock
    private NoteRepository noteRepository;
    @Mock
    private GameSessionRepository gameSessionRepository;
    @Mock
    private CampaignService campaignService;

    @InjectMocks
    private NoteServiceImpl noteService;

    private AppUser owner;
    private Campaign campaign;
    private GameSession gameSession;

    private AuthSession masterSession;
    private AuthSession playerSession;

    @BeforeEach
    void setup() {
        // Arrange
        owner = AppUser.builder().userId(OWNER_ID).username("DM").build();
        campaign = Campaign.builder().campaignId(CAMPAIGN_ID).name("Campaign1").owner(owner).build();
        gameSession = GameSession.builder().sessionId(SESSION_ID).campaign(campaign)
                .joinCode("123123").build();

        masterSession = AuthSession.builder()
                .loginType(LoginType.MASTER).userId(OWNER_ID).displayName("DM").build();

        playerSession = AuthSession.builder()
                .loginType(LoginType.PLAYER).playerId(5L)
                .campaignId(CAMPAIGN_ID).displayName("Omega").build();
    }

    @Test
    void createNote_withNoSession() {
        // Arrange
        when(campaignService.getCampaign(CAMPAIGN_ID)).thenReturn(campaign);
        when(noteRepository.save(any())).thenAnswer(call -> call.getArgument(0));

        // Act
        Note note = noteService.createNote(
                new CreateNoteRequest(CAMPAIGN_ID, null, false, "Campaign note"), masterSession);

        // Assert
        assertNull(note.getGameSession());
        assertEquals(campaign, note.getCampaign());
        verifyNoInteractions(gameSessionRepository); // Verify that no session is queried
    }

    @Test
    void createNote_withSession() {
        // Arrange
        when(campaignService.getCampaign(CAMPAIGN_ID)).thenReturn(campaign);
        when(gameSessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(gameSession));
        when(noteRepository.save(any())).thenAnswer(call -> call.getArgument(0));

        // Act
        Note note = noteService.createNote(
                new CreateNoteRequest(CAMPAIGN_ID, SESSION_ID, true, "Session note"), masterSession);

        // Assert
        assertEquals(gameSession, note.getGameSession());
        assertTrue(note.isSharedWithPlayers());
    }

    @Test
    void createNote_wrongCampaign() {
        // Arrange
        Campaign other = Campaign.builder().campaignId(10L).name("Campaign2").owner(owner).build();
        GameSession foreign = GameSession.builder().sessionId(SESSION_ID).campaign(other).build();

        when(campaignService.getCampaign(CAMPAIGN_ID)).thenReturn(campaign);
        when(gameSessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(foreign));

        // Act - Assert
        assertThrows(NotePermissionException.class, () -> noteService.createNote(
                new CreateNoteRequest(CAMPAIGN_ID, SESSION_ID, false, "Note"), masterSession));
    }

    @Test
    void createNote_onlyMasterAllowed() {
        // Act - Assert
        assertThrows(ResourcePermissionException.class, () -> noteService.createNote(
                new CreateNoteRequest(CAMPAIGN_ID, null, false, "Note"), playerSession));

        verifyNoInteractions(campaignService, noteRepository);
    }

    @Test
    void createNote_onlyOwnerAllowed() {
        // Arrange
        AuthSession otherMaster = AuthSession.builder()
                .loginType(LoginType.MASTER).userId(OTHER_ID).displayName("Other").build();

        when(campaignService.getCampaign(CAMPAIGN_ID)).thenReturn(campaign);

        // Act - Assert
        assertThrows(NotePermissionException.class, () -> noteService.createNote(
                new CreateNoteRequest(CAMPAIGN_ID, null, false, "Note"), otherMaster));
    }

    @Test
    void getCampaignNotes() {
        // Arrange
        when(campaignService.getCampaign(CAMPAIGN_ID)).thenReturn(campaign);
        when(noteRepository.findByCampaign_CampaignIdOrderByCreatedAsc(CAMPAIGN_ID))
                .thenReturn(List.of(note(true), note(false)));

        // Act - Assert
        assertEquals(2, noteService.getCampaignNotes(CAMPAIGN_ID, masterSession).size());
    }

    @Test
    void getCampaignNotes_sharedWithPlayers() {
        // Arrange
        when(noteRepository.findByCampaign_CampaignIdAndSharedWithPlayersTrueOrderByCreatedAsc(CAMPAIGN_ID))
                .thenReturn(List.of(note(true)));

        // Act - Assert
        assertEquals(1, noteService.getCampaignNotes(CAMPAIGN_ID, playerSession).size());
    }

    @Test
    void getCampaignNotes_playerCanOnlyReadOwnCampaign() {
        // Act - Assert
        assertThrows(NotePermissionException.class,
                () -> noteService.getCampaignNotes(10L, playerSession));
    }

    @Test
    void getSessionNotes_sharedWithPlayers() {
        // Arrange
        when(gameSessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(gameSession));
        when(noteRepository.findByGameSession_SessionIdAndSharedWithPlayersTrueOrderByCreatedAsc(SESSION_ID))
                .thenReturn(List.of(note(true)));

        // Act - Assert
        assertEquals(1, noteService.getSessionNotes(SESSION_ID, playerSession).size());
    }

    @Test
    void getSessionNotes_noSession() {
        // Arrange
        when(gameSessionRepository.findById(SESSION_ID)).thenReturn(Optional.empty());

        // Act - Assert
        assertThrows(GameSessionNotFoundException.class,
                () -> noteService.getSessionNotes(SESSION_ID, masterSession));
    }

    @Test
    void updateNote() {
        // Arrange
        Note existing = note(false);
        when(noteRepository.findById(NOTE_ID)).thenReturn(Optional.of(existing));
        when(campaignService.getCampaign(CAMPAIGN_ID)).thenReturn(campaign);
        when(noteRepository.save(any())).thenAnswer(call -> call.getArgument(0));

        // Act
        Note updated = noteService.updateNote(
                new UpdateNoteRequest(NOTE_ID, true, "Updated"), masterSession);

        // Assert
        assertEquals("Updated", updated.getText());
        assertTrue(updated.isSharedWithPlayers());
    }

    @Test
    void deleteNote_missingNote() {
        // Arrange
        when(noteRepository.findById(NOTE_ID)).thenReturn(Optional.empty());

        // Act - Assert
        assertThrows(NoteNotFoundException.class,
                () -> noteService.deleteNote(NOTE_ID, masterSession));
    }

    @Test
    void deleteNote() {
        // Arrange
        Note existing = note(false);
        when(noteRepository.findById(NOTE_ID)).thenReturn(Optional.of(existing));
        when(campaignService.getCampaign(CAMPAIGN_ID)).thenReturn(campaign);

        // Act
        noteService.deleteNote(NOTE_ID, masterSession);

        // Assert
        verify(noteRepository).delete(existing);
    }


    // ----- HELPERS -----

    private Note note(boolean shared) {
        return Note.builder()
                .noteId(NOTE_ID)
                .campaign(campaign)
                .sharedWithPlayers(shared)
                .text("Note text")
                .build();
    }
}