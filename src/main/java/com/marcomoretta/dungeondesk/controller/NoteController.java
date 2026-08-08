package com.marcomoretta.dungeondesk.controller;

import com.marcomoretta.dungeondesk.auth.AuthInterceptor;
import com.marcomoretta.dungeondesk.auth.AuthSession;
import com.marcomoretta.dungeondesk.domain.dto.NoteDto;
import com.marcomoretta.dungeondesk.domain.dto.request.CreateNoteRequestDto;
import com.marcomoretta.dungeondesk.domain.dto.request.UpdateNoteRequestDto;
import com.marcomoretta.dungeondesk.domain.entity.Note;
import com.marcomoretta.dungeondesk.domain.request.CreateNoteRequest;
import com.marcomoretta.dungeondesk.domain.request.UpdateNoteRequest;
import com.marcomoretta.dungeondesk.mapper.NoteMapper;
import com.marcomoretta.dungeondesk.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Class that exposes REST APIs for notes management.
 * Creation and listing are scoped to their container, while a note has an id of its
 * own and is therefore addressable directly for update and delete.
 */
@RestController
@RequestMapping("/api/v1")
public class NoteController {

    private final NoteService noteService;
    private final NoteMapper noteMapper;

    public NoteController(NoteService noteService, NoteMapper noteMapper) {
        this.noteService = noteService;
        this.noteMapper = noteMapper;
    }

    /**
     * Creates a note on a campaign, optionally bound to one of its sessions
     *
     * @param campaignId  The campaign the note belongs to
     * @param dto         The note details
     * @param authSession The client active session
     * @return The created note [201 - CREATED]
     */
    @PostMapping("/campaigns/{campaignId}/notes")
    public ResponseEntity<NoteDto> createNote(
            @PathVariable Long campaignId,
            @Valid @RequestBody CreateNoteRequestDto dto,
            @RequestAttribute(AuthInterceptor.SESSION_ATTRIBUTE) AuthSession authSession) {

        CreateNoteRequest request = noteMapper.fromCreateDto(dto, campaignId);
        Note note = noteService.createNote(request, authSession);

        return ResponseEntity.status(HttpStatus.CREATED).body(noteMapper.toDto(note));
    }

    /**
     * Requests the notes of a campaign, session notes included
     *
     * @param campaignId  The campaign to read
     * @param authSession The client active session
     * @return All the notes for a master, only the shared ones for a player [200 - OK]
     */
    @GetMapping("/campaigns/{campaignId}/notes")
    public ResponseEntity<List<NoteDto>> getCampaignNotes(
            @PathVariable Long campaignId,
            @RequestAttribute(AuthInterceptor.SESSION_ATTRIBUTE) AuthSession authSession) {

        List<Note> notes = noteService.getCampaignNotes(campaignId, authSession);

        return ResponseEntity.ok(noteMapper.toDtoList(notes));
    }

    /**
     * Requests the notes bound to a specific game session
     *
     * @param sessionId   The session to read
     * @param authSession The client active session
     * @return All the notes for a master, only the shared ones for a player [200 - OK]
     */
    @GetMapping("/sessions/{sessionId}/notes")
    public ResponseEntity<List<NoteDto>> getSessionNotes(
            @PathVariable Long sessionId,
            @RequestAttribute(AuthInterceptor.SESSION_ATTRIBUTE) AuthSession authSession) {

        List<Note> notes = noteService.getSessionNotes(sessionId, authSession);

        return ResponseEntity.ok(noteMapper.toDtoList(notes));
    }

    /**
     * Requests a note content or visibility update
     *
     * @param noteId      The note to update
     * @param dto         The update details
     * @param authSession The client active session
     * @return The updated note [200 - OK]
     */
    @PutMapping("/notes/{noteId}")
    public ResponseEntity<NoteDto> updateNote(
            @PathVariable Long noteId,
            @Valid @RequestBody UpdateNoteRequestDto dto,
            @RequestAttribute(AuthInterceptor.SESSION_ATTRIBUTE) AuthSession authSession) {

        UpdateNoteRequest request = noteMapper.fromUpdateDto(dto, noteId);
        Note note = noteService.updateNote(request, authSession);

        return ResponseEntity.ok(noteMapper.toDto(note));
    }

    /**
     * Requests a note deletion
     *
     * @param noteId      The note to delete
     * @param authSession The client active session
     * @return No content [204 - NO CONTENT]
     */
    @DeleteMapping("/notes/{noteId}")
    public ResponseEntity<Void> deleteNote(
            @PathVariable Long noteId,
            @RequestAttribute(AuthInterceptor.SESSION_ATTRIBUTE) AuthSession authSession) {

        noteService.deleteNote(noteId, authSession);

        return ResponseEntity.noContent().build();
    }
}
