package com.marcomoretta.dungeondesk.controller;

import com.marcomoretta.dungeondesk.auth.AuthSession;
import com.marcomoretta.dungeondesk.auth.LoginType;

import com.marcomoretta.dungeondesk.domain.dto.SheetDto;
import com.marcomoretta.dungeondesk.domain.dto.request.SheetRequestDto;
import com.marcomoretta.dungeondesk.domain.entity.CharacterSheet;
import com.marcomoretta.dungeondesk.domain.request.SheetRequest;
import com.marcomoretta.dungeondesk.event.GameEventStream;
import com.marcomoretta.dungeondesk.event.MapChangedEvent;
import com.marcomoretta.dungeondesk.event.SheetChangedEvent;
import com.marcomoretta.dungeondesk.exception.ResourcePermissionException;

import com.marcomoretta.dungeondesk.mapper.SheetMapper;
import com.marcomoretta.dungeondesk.service.SheetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SheetControllerTest {

    // Using fixed ids for ease of use
    private final static Long OWNER_ID = 1L;
    private final static Long CAMPAIGN_ID = 2L;
    private final static Long PLAYER_ID = 3L;
    private final static Long SHEET_ID = 4L;

    @Mock
    private SheetService sheetService;
    @Mock
    private SheetMapper sheetMapper;
    @Mock
    private GameEventStream gameEventStream;

    @InjectMocks
    private SheetController controller;

    private CharacterSheet characterSheet;
    private SheetDto sheetDto;

    private AuthSession masterSession;
    private AuthSession playerSession;

    @BeforeEach
    void setup() {
        // Arrange
        characterSheet = CharacterSheet.builder()
                .sheetId(SHEET_ID).name("Yoshi").maxHp(20).currentHp(18).build();

        sheetDto = SheetDto.builder()
                .sheetId(SHEET_ID).sheetType("CHARACTER").name("Yoshi").build();

        masterSession = AuthSession.builder()
                .loginType(LoginType.MASTER).userId(OWNER_ID).displayName("DM").build();

        playerSession = AuthSession.builder()
                .loginType(LoginType.PLAYER).playerId(PLAYER_ID)
                .campaignId(CAMPAIGN_ID).displayName("Omega").build();
    }

    @Test
    void getSheet() {
        // Arrange
        when(sheetService.getSheet(SHEET_ID, masterSession)).thenReturn(characterSheet);
        when(sheetMapper.toDto(characterSheet)).thenReturn(sheetDto);

        // Act
        ResponseEntity<SheetDto> response = controller.getSheet(SHEET_ID, masterSession);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Yoshi", response.getBody().name());
    }

    @Test
    void getSheetsSummary() {
        // Arrange
        when(sheetService.getCampaignSheets(playerSession)).thenReturn(List.of());
        when(sheetMapper.toSummaryDtoList(List.of())).thenReturn(List.of());

        // Act - Assert
        assertEquals(HttpStatus.OK,
                controller.getSheetsSummary(playerSession).getStatusCode());
    }

    @Test
    void getOwnedSheets_onlyMasterAllowed() {
        // Act - Assert
        assertThrows(ResourcePermissionException.class,
                () -> controller.getOwnedSheets(playerSession));

        verifyNoInteractions(sheetService);
    }

    @Test
    void createCharacterSheet() {
        // Arrange
        SheetRequestDto dto = SheetRequestDto.builder().name("Yoshi").campaignId(CAMPAIGN_ID).build();
        SheetRequest request = SheetRequest.builder().name("Yoshi").campaignId(CAMPAIGN_ID).build();

        when(sheetMapper.fromDto(dto, null, OWNER_ID)).thenReturn(request);
        when(sheetService.createCharacterSheet(request, masterSession)).thenReturn(characterSheet);
        when(sheetMapper.toDto(characterSheet)).thenReturn(sheetDto);

        // Act
        ResponseEntity<SheetDto> response = controller.createCharacterSheet(dto, masterSession);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("/api/v1/sheets/" + SHEET_ID, response.getHeaders().getLocation().toString());
    }

    @Test
    void createCharacterSheet_onlyMasterAllowed() {
        // Act - Assert
        assertThrows(ResourcePermissionException.class,
                () -> controller.createCharacterSheet(null, playerSession));

        verifyNoInteractions(sheetService, sheetMapper);
    }

    @Test
    void updateSheet() {
        // Arrange
        SheetRequestDto dto = SheetRequestDto.builder().name("Yoshi").currentHp(12).build();
        SheetRequest request = SheetRequest.builder().name("Yoshi").currentHp(12).build();

        when(sheetMapper.fromDto(dto, SHEET_ID, null)).thenReturn(request);
        when(sheetService.updateSheet(request, playerSession)).thenReturn(characterSheet);
        when(sheetMapper.toDto(characterSheet)).thenReturn(sheetDto);

        // Act - Assert
        assertEquals(HttpStatus.OK,
                controller.updateSheet(SHEET_ID, dto, playerSession).getStatusCode());

        verify(gameEventStream).notifyObservers(any(SheetChangedEvent.class));
    }

    @Test
    void deleteSheet() {
        // Act - Assert
        assertEquals(HttpStatus.NO_CONTENT,
                controller.deleteSheet(SHEET_ID, masterSession).getStatusCode());

        verify(sheetService).deleteSheet(SHEET_ID, masterSession);
        // Removing a sheet takes its token away through the cascade
        verify(gameEventStream).notifyObservers(any(MapChangedEvent.class));
    }
}