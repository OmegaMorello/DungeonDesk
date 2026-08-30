package com.marcomoretta.dungeondesk.service.impl;

import com.marcomoretta.dungeondesk.auth.AuthSession;
import com.marcomoretta.dungeondesk.auth.LoginType;
import com.marcomoretta.dungeondesk.domain.entity.*;
import com.marcomoretta.dungeondesk.domain.request.SheetRequest;
import com.marcomoretta.dungeondesk.exception.SheetPermissionException;
import com.marcomoretta.dungeondesk.repository.PlayerRepository;
import com.marcomoretta.dungeondesk.repository.SheetRepository;
import com.marcomoretta.dungeondesk.service.AppUserService;
import com.marcomoretta.dungeondesk.service.CampaignService;
import com.marcomoretta.dungeondesk.service.GameSessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SheetServiceImplTest {

    // Using fixed ids for ease of use
    private final static Long OWNER_ID = 1L;
    private final static Long CAMPAIGN_ID = 2L;
    private final static Long PLAYER_ID = 3L;
    private final static Long SHEET_ID = 4L;

    @Mock
    private SheetRepository sheetRepository;
    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private AppUserService appUserService;
    @Mock
    private CampaignService campaignService;
    @Mock
    private GameSessionService gameSessionService;

    @InjectMocks
    private SheetServiceImpl sheetService;

    private AppUser owner;
    private Campaign campaign;
    private Player player;

    private AuthSession masterSession;
    private AuthSession playerSession;

    @BeforeEach
    void setup() {
        // Arrange
        owner = AppUser.builder().userId(OWNER_ID).username("DM").build();
        campaign = Campaign.builder().campaignId(CAMPAIGN_ID).name("Campaign1").owner(owner).build();
        player = Player.builder().playerId(PLAYER_ID).name("Omega").campaign(campaign).build();

        masterSession = AuthSession.builder()
                .loginType(LoginType.MASTER)
                .userId(OWNER_ID)
                .displayName(owner.getUsername())
                .build();

        playerSession = AuthSession.builder()
                .loginType(LoginType.PLAYER)
                .playerId(PLAYER_ID)
                .campaignId(campaign.getCampaignId())
                .displayName(player.getName())
                .build();
    }

    @Test
    void createCharacterSheet() {
        // Arrange
        when(appUserService.getUser(OWNER_ID)).thenReturn(owner);
        when(campaignService.getCampaign(CAMPAIGN_ID)).thenReturn(campaign);
        when(playerRepository.findById(PLAYER_ID)).thenReturn(Optional.of(player));
        when(sheetRepository.save(any())).thenAnswer(call -> call.getArgument(0));

        // Act
        GenericSheet sheet = sheetService.createCharacterSheet(
                request(null, PLAYER_ID, "Mario"), masterSession);

        // Assert
        assertInstanceOf(CharacterSheet.class, sheet);
        assertEquals("Mario", sheet.getName());
        assertEquals(owner, sheet.getOwner());
        assertEquals(campaign, sheet.getCampaign());
        assertEquals(player, ((CharacterSheet) sheet).getPlayer()); // Casting to get the player
    }

    @Test
    void createEnemySheet() {
        // Arrange
        when(appUserService.getUser(OWNER_ID)).thenReturn(owner);
        when(campaignService.getCampaign(CAMPAIGN_ID)).thenReturn(campaign);
        when(sheetRepository.save(any())).thenAnswer(call -> call.getArgument(0));

        // Act
        GenericSheet sheet = sheetService.createEnemySheet(
                request(null, null, "Goblin"), masterSession);

        // Assert
        assertInstanceOf(EnemySheet.class, sheet);
        verifyNoInteractions(playerRepository);
    }

    @Test
    void getSheet_masterReadsOwnLibrary() {
        // Arrange
        CharacterSheet sheet = characterSheet();
        when(sheetRepository.findById(SHEET_ID)).thenReturn(Optional.of(sheet));

        // Act - Assert
        assertEquals(sheet, sheetService.getSheet(SHEET_ID, masterSession));
    }

    @Test
    void getSheet_masterCanOnlyReadOwnLibrary() {
        // Arrange
        CharacterSheet sheet = characterSheet();
        sheet.setOwner(AppUser.builder().userId(99L).username("other").build());
        when(sheetRepository.findById(SHEET_ID)).thenReturn(Optional.of(sheet));

        // Act - Assert
        assertThrows(SheetPermissionException.class,
                () -> sheetService.getSheet(SHEET_ID, masterSession));
    }

    @Test
    void getSheet_playerReadsACharacterOfTheirCampaign() {
        // Arrange
        CharacterSheet sheet = characterSheet();
        when(sheetRepository.findById(SHEET_ID)).thenReturn(Optional.of(sheet));

        // Act - Assert
        assertEquals(sheet, sheetService.getSheet(SHEET_ID, playerSession));
    }

    @Test
    void getSheet_playerCannotReadAnotherCampaign() {
        // Arrange
        Campaign campaign2 = Campaign.builder().campaignId(10L).name("Campaign2").owner(owner).build();
        CharacterSheet sheet = characterSheet();
        // Assign the player to a different campaign
        sheet.setPlayer(Player.builder().playerId(18L).name("Toad").campaign(campaign2).build());

        when(sheetRepository.findById(SHEET_ID)).thenReturn(Optional.of(sheet));

        // Act - Assert
        assertThrows(SheetPermissionException.class,
                () -> sheetService.getSheet(SHEET_ID, playerSession));
    }

    @Test
    void getSheet_playerCannotReadAnEnemy() {
        // Arrange
        EnemySheet sheet = EnemySheet.builder()
                .sheetId(SHEET_ID).owner(owner).campaign(campaign).name("Goblin").build();

        when(sheetRepository.findById(SHEET_ID)).thenReturn(Optional.of(sheet));

        // Act - Assert
        assertThrows(SheetPermissionException.class,
                () -> sheetService.getSheet(SHEET_ID, playerSession));
    }

    @Test
    void getCampaignSheets_masterCanSeeEnemies() {
        // Arrange
        GameSession session = GameSession.builder().sessionId(1L).campaign(campaign).build();
        when(gameSessionService.getActiveSession()).thenReturn(Optional.of(session));
        when(sheetRepository.findByCampaign_CampaignIdOrderByNameAsc(CAMPAIGN_ID))
                .thenReturn(List.of(characterSheet(), enemySheet()));

        // Act - Assert
        assertEquals(2, sheetService.getCampaignSheets(masterSession).size());
    }

    @Test
    void getCampaignSheets_playerCannotSeeEnemies() {
        // Arrange
        when(sheetRepository.findByCampaign_CampaignIdOrderByNameAsc(CAMPAIGN_ID))
                .thenReturn(List.of(characterSheet(), enemySheet()));

        // Act
        List<GenericSheet> sheets = sheetService.getCampaignSheets(playerSession);

        // Assert
        assertEquals(1, sheets.size());
        assertInstanceOf(CharacterSheet.class, sheets.getFirst());
    }

    @Test
    void getCampaignSheets_masterWithNoSession() {
        // Arrange
        when(gameSessionService.getActiveSession()).thenReturn(Optional.empty());

        // Assert
        assertTrue(sheetService.getCampaignSheets(masterSession).isEmpty());
        verifyNoInteractions(sheetRepository);
    }

    @Test
    void updateSheet_playerEditsTheirOwnCharacter() {
        // Arrange
        CharacterSheet characterSheet = characterSheet();
        when(sheetRepository.findById(SHEET_ID)).thenReturn(Optional.of(characterSheet));
        when(sheetRepository.save(any())).thenAnswer(call -> call.getArgument(0));

        // Act
        GenericSheet sheet = sheetService.updateSheet(
                request(SHEET_ID, PLAYER_ID, "Updated"), playerSession);

        // Assert
        assertEquals("Updated", sheet.getName());
    }

    @Test
    void updateSheet_playerCanOnlyEditOwnCharacter() {
        // Arrange
        CharacterSheet sheet = characterSheet();
        sheet.setPlayer(Player.builder().playerId(8L).name("Shy Guy").campaign(campaign).build());
        when(sheetRepository.findById(SHEET_ID)).thenReturn(Optional.of(sheet));

        // Act - Assert
        assertThrows(SheetPermissionException.class, () -> sheetService.updateSheet(
                request(SHEET_ID, PLAYER_ID, "Stolen"), playerSession));

        verify(sheetRepository, never()).save(any()); // Verify that no save command is executed
    }

    @Test
    void deleteSheet_onlyTheOwnerCanDelete() {
        // Arrange
        CharacterSheet sheet = characterSheet();
        when(sheetRepository.findById(SHEET_ID)).thenReturn(Optional.of(sheet));

        // Act
        sheetService.deleteSheet(SHEET_ID, masterSession);

        // Assert
        verify(sheetRepository).delete(sheet); // Verify that the delete command was executed
    }

    @Test
    void deleteSheet_playerCannotDelete() {
        // Arrange
        CharacterSheet sheet = characterSheet();
        when(sheetRepository.findById(SHEET_ID)).thenReturn(Optional.of(sheet));

        // Act - Assert
        assertThrows(SheetPermissionException.class,
                () -> sheetService.deleteSheet(SHEET_ID, playerSession));

        verify(sheetRepository, never()).delete(any()); // Verify that no delete command is executed
    }

    @Test
    void getOwnedSheets() {
        // Arrange
        when(sheetRepository.findByOwner_UserIdOrderByNameAsc(OWNER_ID))
                .thenReturn(List.of(characterSheet()));

        // Act - Assert
        assertEquals(1, sheetService.getOwnedSheets(masterSession).size());
    }


    // ----- HELPERS -----

    private CharacterSheet characterSheet() {
        return CharacterSheet.builder()
                .sheetId(SHEET_ID)
                .owner(owner)
                .campaign(campaign)
                .player(player)
                .name("Luigi")
                .build();
    }

    private EnemySheet enemySheet() {
        return EnemySheet.builder()
                .sheetId(20L)
                .owner(owner)
                .campaign(campaign)
                .name("Bowser")
                .build();
    }

    private SheetRequest request(Long sheetId, Long playerId, String name) {
        return SheetRequest.builder()
                .sheetId(sheetId)
                .ownerId(OWNER_ID)
                .campaignId(CAMPAIGN_ID)
                .name(name)
                .playerId(playerId)
                .armorClass(12).maxHp(20).currentHp(18).speed(30)
                .strength(14).dexterity(16).constitution(12)
                .intelligence(10).wisdom(8).charisma(13)
                .proficiencyBonus(2)
                .skillProficiencies(Set.of())
                .skillExpertise(Set.of())
                .savingThrowProficiencies(Set.of())
                .spellSlots(List.of())
                .attacks(List.of())
                .notes("")
                .build();
    }
}