package com.marcomoretta.dungeondesk.mapper.impl;

import com.marcomoretta.dungeondesk.domain.CampaignExport;
import com.marcomoretta.dungeondesk.domain.dto.CampaignDto;
import com.marcomoretta.dungeondesk.domain.dto.CampaignExportDto;
import com.marcomoretta.dungeondesk.domain.dto.request.AddPlayerRequestDto;
import com.marcomoretta.dungeondesk.domain.dto.request.CreateCampaignRequestDto;
import com.marcomoretta.dungeondesk.domain.dto.request.RenamePlayerRequestDto;
import com.marcomoretta.dungeondesk.domain.dto.request.UpdateCampaignRequestDto;
import com.marcomoretta.dungeondesk.domain.entity.AppUser;
import com.marcomoretta.dungeondesk.domain.entity.Campaign;
import com.marcomoretta.dungeondesk.domain.entity.Player;
import com.marcomoretta.dungeondesk.domain.request.AddPlayerRequest;
import com.marcomoretta.dungeondesk.domain.request.CreateCampaignRequest;
import com.marcomoretta.dungeondesk.domain.request.RenamePlayerRequest;
import com.marcomoretta.dungeondesk.domain.request.UpdateCampaignRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CampaignMapperImplTest {

    private final NoteMapperImpl noteMapper = new NoteMapperImpl();
    private final GameSessionMapperImpl gameSessionMapper = new GameSessionMapperImpl();
    private final SheetMapperImpl sheetMapper = new SheetMapperImpl();
    private final CampaignMapperImpl campaignMapper = new CampaignMapperImpl(noteMapper, gameSessionMapper, sheetMapper);

    // Arrange
    private final Campaign campaign1 = Campaign.builder()
            .name("DungeonDesk")
            .description("DungeonDeskription")
            .owner(AppUser.builder().build())
            .players(List.of(
                    Player.builder().name("Player1").build(),
                    Player.builder().name("Player2").build()
            ))
            .build();

    private final Campaign campaign2 = Campaign.builder()
            .name("DeskDungeon")
            .description("DeskriptionDungeon")
            .owner(AppUser.builder().build())
            .players(List.of(
                    Player.builder().name("Player3").build(),
                    Player.builder().name("Player4").build(),
                    Player.builder().name("Player5").build()
            ))
            .build();

    @Test
    void toDto() {
        // Act
        CampaignDto campaignDto = campaignMapper.toDto(campaign1);

        // Assert
        assertEquals("DungeonDesk", campaignDto.name());
        assertEquals(2, campaignDto.players().size());
    }

    @Test
    void toDtoList() {
        // Act
        List<CampaignDto> campaignDtoList = campaignMapper.toDtoList(List.of(campaign1, campaign2));

        // Assert
        assertEquals("DungeonDeskription", campaignDtoList.getFirst().description());
        assertEquals(3, campaignDtoList.getLast().players().size());
        assertEquals(2, campaignDtoList.size());
    }

    @Test
    void fromCreateDto() {
        // Arrange
        CreateCampaignRequestDto createCampaignRequestDto = new CreateCampaignRequestDto("Campaign1", "Desc1");

        // Act
        CreateCampaignRequest createCampaignRequest = campaignMapper.fromCreateDto(createCampaignRequestDto, 1L);

        // Assert
        assertEquals("Campaign1", createCampaignRequest.name());
        assertEquals("Desc1", createCampaignRequest.description());
        assertEquals(1, createCampaignRequest.ownerId());
    }

    @Test
    void fromUpdateDto() {
        // Arrange
        UpdateCampaignRequestDto updateCampaignRequestDto = new UpdateCampaignRequestDto("CampaignUpdate", "DescUpdate");

        // Act
        UpdateCampaignRequest updateCampaignRequest = campaignMapper.fromUpdateDto(updateCampaignRequestDto, 1L);

        // Assert
        assertEquals("CampaignUpdate", updateCampaignRequest.name());
        assertEquals("DescUpdate", updateCampaignRequest.description());
        assertEquals(1, updateCampaignRequest.campaignId());
    }

    @Test
    void fromAddPlayerDto() {
        // Arrange
        AddPlayerRequestDto addPlayerRequestDto = new AddPlayerRequestDto("NewPlayer");

        // Act
        AddPlayerRequest addPlayerRequest = campaignMapper.fromAddPlayerDto(addPlayerRequestDto, 1L);

        // Assert
        assertEquals("NewPlayer", addPlayerRequest.name());
        assertEquals(1, addPlayerRequest.campaignId());
    }

    @Test
    void fromRenamePlayerDto() {
        // Arrange
        RenamePlayerRequestDto renamePlayerRequestDto = new RenamePlayerRequestDto("RenamedPlayer");

        // Act
        RenamePlayerRequest renamePlayerRequest = campaignMapper.fromRenamePlayerDto(
                renamePlayerRequestDto,
                1L,
                2L);

        // Assert
        assertEquals("RenamedPlayer", renamePlayerRequest.name());
        assertEquals(1, renamePlayerRequest.campaignId());
        assertEquals(2, renamePlayerRequest.playerId());
    }

    @Test
    void toExportDto() {
        // Arrange
        CampaignExport campaignExport = new CampaignExport(campaign1, List.of(), List.of(), List.of());

        // Act
        CampaignExportDto campaignExportDto = campaignMapper.toExportDto(campaignExport);

        // Assert
        assertNotNull(campaignExportDto.noteDtos());
        assertNotNull(campaignExportDto.gameSessionDtos());
        assertNotNull(campaignExportDto.sheetDtos());
        assertEquals(2, campaignExportDto.playerDtos().size());
        assertEquals("DungeonDesk", campaignExportDto.campaignDto().name());
    }
}