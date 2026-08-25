package com.marcomoretta.dungeondesk.domain;

import com.marcomoretta.dungeondesk.domain.entity.Campaign;
import com.marcomoretta.dungeondesk.domain.entity.GameSession;
import com.marcomoretta.dungeondesk.domain.entity.GenericSheet;
import com.marcomoretta.dungeondesk.domain.entity.Note;

import java.util.List;

/**
 * Every entity contained in a campaign, ready for export
 *
 * @param campaign     The campaign to be exported. Contains the players list as well
 * @param notes        The campaign notes
 * @param gameSessions The campaign game sessions
 * @param sheets       The campaign PC and NPC sheets
 */
public record CampaignExport(
        Campaign campaign,
        List<Note> notes,
        List<GameSession> gameSessions,
        List<GenericSheet> sheets
) {
}
