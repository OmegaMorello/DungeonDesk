package com.marcomoretta.dungeondesk.mapper;

import com.marcomoretta.dungeondesk.domain.dto.NoteDto;
import com.marcomoretta.dungeondesk.domain.dto.request.CreateNoteRequestDto;
import com.marcomoretta.dungeondesk.domain.dto.request.UpdateNoteRequestDto;
import com.marcomoretta.dungeondesk.domain.entity.Note;
import com.marcomoretta.dungeondesk.domain.request.CreateNoteRequest;
import com.marcomoretta.dungeondesk.domain.request.UpdateNoteRequest;

import java.util.List;

/**
 * Note DTO mapper interface
 */
public interface NoteMapper {

    /**
     * Maps a note create request from a dto, merging in the campaign id taken from the path
     *
     * @param dto        Presentation layer request dto
     * @param campaignId The campaign the note belongs to
     * @return The service layer request
     */
    CreateNoteRequest fromCreateDto(CreateNoteRequestDto dto, Long campaignId);

    /**
     * Maps a note update request from a dto, merging in the note id taken from the path
     *
     * @param dto    Presentation layer request dto
     * @param noteId The note to update
     * @return The service layer request
     */
    UpdateNoteRequest fromUpdateDto(UpdateNoteRequestDto dto, Long noteId);

    /**
     * Maps a note to a dto
     *
     * @param note Service layer note
     * @return Presentation layer note dto
     */
    NoteDto toDto(Note note);

    /**
     * Maps a note list to a dto list
     *
     * @param noteList Service layer note list
     * @return Presentation layer note dto list
     */
    List<NoteDto> toDtoList(List<Note> noteList);
}
