package com.marcomoretta.dungeondesk.mapper.impl;

import com.marcomoretta.dungeondesk.domain.dto.NoteDto;
import com.marcomoretta.dungeondesk.domain.dto.request.CreateNoteRequestDto;
import com.marcomoretta.dungeondesk.domain.dto.request.UpdateNoteRequestDto;
import com.marcomoretta.dungeondesk.domain.entity.AppUser;
import com.marcomoretta.dungeondesk.domain.entity.Campaign;
import com.marcomoretta.dungeondesk.domain.entity.Note;
import com.marcomoretta.dungeondesk.domain.entity.Player;
import com.marcomoretta.dungeondesk.domain.request.CreateNoteRequest;
import com.marcomoretta.dungeondesk.domain.request.UpdateNoteRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NoteMapperImplTest {

    private final NoteMapperImpl noteMapper = new NoteMapperImpl();

    @Test
    void toDto() {
        // Arrange
        Note note = Note.builder()
                .campaign(Campaign.builder()
                        .name("TestCampaign")
                        .description("CampaignDesc")
                        .owner(AppUser.builder().build())
                        .players(List.of(Player.builder().name("P1").build(),
                                Player.builder().name("P2").build()))
                        .build())
                .sharedWithPlayers(true)
                .text("Test note")
                .build();

        // Act
        NoteDto noteDto = noteMapper.toDto(note);

        // Assert
        assertEquals("Test note", noteDto.text());
        assertTrue(noteDto.sharedWithPlayers());
        assertNull(noteDto.sessionId());
    }

    @Test
    void fromCreateDto() {
        // Arrange
        CreateNoteRequestDto createNoteRequestDto = new CreateNoteRequestDto(1L, false, "create text");

        // Act
        CreateNoteRequest createNoteRequest = noteMapper.fromCreateDto(createNoteRequestDto, 2L);

        // Assert
        assertFalse(createNoteRequest.sharedWithPlayers());
        assertEquals("create text", createNoteRequest.text());
        assertEquals(2, createNoteRequest.campaignId());
        assertEquals(1, createNoteRequest.gameSessionId());
    }

    @Test
    void fromUpdateDto() {
        // Arrange
        UpdateNoteRequestDto updateNoteRequestDto = new UpdateNoteRequestDto(true, "update text");

        // Act
        UpdateNoteRequest updateNoteRequest = noteMapper.fromUpdateDto(updateNoteRequestDto, 7L);

        // Assert
        assertTrue(updateNoteRequest.sharedWithPlayers());
        assertEquals("update text", updateNoteRequest.text());
        assertEquals(7, updateNoteRequest.noteId());
    }

}