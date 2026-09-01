package com.marcomoretta.dungeondesk.service;

import com.marcomoretta.dungeondesk.auth.AuthSession;
import com.marcomoretta.dungeondesk.domain.entity.GenericSheet;
import com.marcomoretta.dungeondesk.domain.request.SheetRequest;

import java.util.List;

/**
 * Interface that defines the Sheet Service, for both kinds of sheet
 * Writing is reserved to the owner or the player the sheet is assigned to
 */
public interface SheetService {

    /**
     * Creates a player character sheet
     *
     * @param request The sheet content
     * @param session The caller session
     * @return The newly created sheet
     */
    GenericSheet createCharacterSheet(SheetRequest request, AuthSession session);

    /**
     * Creates an enemy sheet in the Dungeon Master library
     *
     * @param request The sheet content
     * @param session The caller session
     * @return The newly created sheet
     */
    GenericSheet createEnemySheet(SheetRequest request, AuthSession session);

    /**
     * Reads the sheets of the current campaign
     *
     * @param session The caller session
     * @return The sheet
     */
    List<GenericSheet> getCampaignSheets(AuthSession session);

    /**
     * Reads a sheet of either kind
     *
     * @param sheetId The sheet to read
     * @param session The caller session
     * @return The sheet
     */
    GenericSheet getSheet(Long sheetId, AuthSession session);

    /**
     * Lists every sheet in the caller library
     *
     * @param session The caller session
     * @return The sheets, ordered by name
     */
    List<GenericSheet> getOwnedSheets(AuthSession session);

    /**
     * Updates a sheet of either kind, the type cannot be changed
     *
     * @param request The updated content, carrying the sheet id
     * @param session The caller session
     * @return The updated sheet
     */
    GenericSheet updateSheet(SheetRequest request, AuthSession session);

    /**
     * Deletes a sheet
     *
     * @param sheetId The sheet to delete
     * @param session The caller session
     */
    void deleteSheet(Long sheetId, AuthSession session);
}
