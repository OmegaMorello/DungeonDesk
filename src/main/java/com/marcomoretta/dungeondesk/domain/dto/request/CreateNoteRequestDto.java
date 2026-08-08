package com.marcomoretta.dungeondesk.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Dto needed to create a note.
 *
 * @param sessionId         Optionally binds the note to a specific game session
 * @param sharedWithPlayers Whether the players may read the note
 * @param text              The note content
 */
public record CreateNoteRequestDto(
        Long sessionId,

        boolean sharedWithPlayers,

        @NotBlank(message = EMPTY_TEXT)
        @Size(max = 4000, message = TEXT_TOO_LONG)
        String text
) {
    /**
     * Trims (strips) the text during construction
     */
    public CreateNoteRequestDto {
        if (text != null) text = text.strip();
    }

    private static final String EMPTY_TEXT = "A note cannot be empty";
    private static final String TEXT_TOO_LONG = "A note cannot exceed 4000 characters";
}
