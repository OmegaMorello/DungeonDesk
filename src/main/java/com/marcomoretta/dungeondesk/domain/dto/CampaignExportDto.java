package com.marcomoretta.dungeondesk.domain.dto;

import java.util.List;

/**
 * Dto to exposes a campaign export downloadable file
 *
 * @param campaignDto     The campaign dto
 * @param noteDtos        The campaign notes dto list
 * @param gameSessionDtos The campaign game session dto list
 * @param playerDtos      The campaign players dto list
 * @param sheetDtos       The campaign PC and NPC sheets dto list
 */
public record CampaignExportDto(
        CampaignDto campaignDto,
        List<NoteDto> noteDtos,
        List<GameSessionDto> gameSessionDtos,
        List<PlayerDto> playerDtos,
        List<SheetDto> sheetDtos
) {
}
