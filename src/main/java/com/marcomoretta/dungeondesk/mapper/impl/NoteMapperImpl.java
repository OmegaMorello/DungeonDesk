package com.marcomoretta.dungeondesk.mapper.impl;

import com.marcomoretta.dungeondesk.domain.dto.NoteDto;
import com.marcomoretta.dungeondesk.domain.dto.request.CreateNoteRequestDto;
import com.marcomoretta.dungeondesk.domain.dto.request.UpdateNoteRequestDto;
import com.marcomoretta.dungeondesk.domain.entity.Note;
import com.marcomoretta.dungeondesk.domain.request.CreateNoteRequest;
import com.marcomoretta.dungeondesk.domain.request.UpdateNoteRequest;
import com.marcomoretta.dungeondesk.mapper.NoteMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper for the Note to and from Dto
 */
@Component
public class NoteMapperImpl implements NoteMapper {

    @Override
    public CreateNoteRequest fromCreateDto(CreateNoteRequestDto dto, Long campaignId) {
        return new CreateNoteRequest(
                campaignId,
                dto.sessionId(),
                dto.sharedWithPlayers(),
                dto.text()
        );
    }

    @Override
    public UpdateNoteRequest fromUpdateDto(UpdateNoteRequestDto dto, Long noteId) {
        return new UpdateNoteRequest(
                noteId,
                dto.sharedWithPlayers(),
                dto.text()
        );
    }

    @Override
    public NoteDto toDto(Note note) {
        return new NoteDto(
                note.getNoteId(),
                note.getCampaign().getCampaignId(),
                // gameSession is null by design on a general campaign note
                note.getGameSession() != null ? note.getGameSession().getSessionId() : null,
                note.isSharedWithPlayers(),
                note.getText(),
                note.getCreated()
        );
    }

    @Override
    public List<NoteDto> toDtoList(List<Note> noteList) {
        return noteList.stream().map(this::toDto).toList();
    }
}
