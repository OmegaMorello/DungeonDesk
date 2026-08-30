package com.marcomoretta.dungeondesk.controller;

import com.marcomoretta.dungeondesk.auth.AuthSession;
import com.marcomoretta.dungeondesk.auth.LoginType;
import com.marcomoretta.dungeondesk.domain.dto.NoteDto;
import com.marcomoretta.dungeondesk.domain.dto.request.CreateNoteRequestDto;
import com.marcomoretta.dungeondesk.domain.entity.Note;
import com.marcomoretta.dungeondesk.domain.request.CreateNoteRequest;
import com.marcomoretta.dungeondesk.mapper.NoteMapper;
import com.marcomoretta.dungeondesk.service.NoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class NoteControllerTest {

    private final static Long CAMPAIGN_ID = 2L;
    private final static Long SESSION_ID = 3L;
    private final static Long NOTE_ID = 4L;

    @Mock
    private NoteService noteService;
    @Mock
    private NoteMapper noteMapper;

    @InjectMocks
    private NoteController controller;

    private Note note;
    private NoteDto noteDto;
    private AuthSession masterSession;

    @BeforeEach
    void setup() {
        // Arrange
        note = Note.builder().noteId(NOTE_ID).text("Note text").build();
        noteDto = new NoteDto(NOTE_ID, CAMPAIGN_ID, null, false, "Note text", Instant.now());

        masterSession = AuthSession.builder()
                .loginType(LoginType.MASTER).userId(1L).displayName("DM").build();
    }

    @Test
    void createNote() {
        // Arrange
        CreateNoteRequestDto dto = new CreateNoteRequestDto(null, false, "Note text");
        CreateNoteRequest request = new CreateNoteRequest(CAMPAIGN_ID, null, false, "Note text");

        when(noteMapper.fromCreateDto(dto, CAMPAIGN_ID)).thenReturn(request);
        when(noteService.createNote(request, masterSession)).thenReturn(note);
        when(noteMapper.toDto(note)).thenReturn(noteDto);

        // Act - Assert
        assertEquals(HttpStatus.CREATED,
                controller.createNote(CAMPAIGN_ID, dto, masterSession).getStatusCode());
    }

    @Test
    void getCampaignNotes() {
        // Arrange
        when(noteService.getCampaignNotes(CAMPAIGN_ID, masterSession)).thenReturn(List.of(note));
        when(noteMapper.toDtoList(List.of(note))).thenReturn(List.of(noteDto));

        // Act - Assert
        assertEquals(1, controller.getCampaignNotes(CAMPAIGN_ID, masterSession).getBody().size());
    }

    @Test
    void getSessionNotes() {
        // Arrange
        when(noteService.getSessionNotes(SESSION_ID, masterSession)).thenReturn(List.of());
        when(noteMapper.toDtoList(List.of())).thenReturn(List.of());

        // Act - Assert
        assertEquals(HttpStatus.OK,
                controller.getSessionNotes(SESSION_ID, masterSession).getStatusCode());
    }

    @Test
    void deleteNote() {
        // Act - Assert
        assertEquals(HttpStatus.NO_CONTENT,
                controller.deleteNote(NOTE_ID, masterSession).getStatusCode());

        verify(noteService).deleteNote(NOTE_ID, masterSession);
    }
}