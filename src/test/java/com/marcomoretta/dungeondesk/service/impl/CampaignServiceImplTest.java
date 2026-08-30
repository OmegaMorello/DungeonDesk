package com.marcomoretta.dungeondesk.service.impl;

import com.marcomoretta.dungeondesk.domain.CampaignExport;
import com.marcomoretta.dungeondesk.domain.entity.AppUser;
import com.marcomoretta.dungeondesk.domain.entity.Campaign;
import com.marcomoretta.dungeondesk.domain.entity.Note;
import com.marcomoretta.dungeondesk.domain.entity.Player;
import com.marcomoretta.dungeondesk.domain.request.AddPlayerRequest;
import com.marcomoretta.dungeondesk.domain.request.CreateCampaignRequest;
import com.marcomoretta.dungeondesk.domain.request.RenamePlayerRequest;
import com.marcomoretta.dungeondesk.domain.request.UpdateCampaignRequest;
import com.marcomoretta.dungeondesk.exception.*;
import com.marcomoretta.dungeondesk.repository.CampaignRepository;
import com.marcomoretta.dungeondesk.repository.GameSessionRepository;
import com.marcomoretta.dungeondesk.repository.NoteRepository;
import com.marcomoretta.dungeondesk.repository.SheetRepository;
import com.marcomoretta.dungeondesk.service.AppUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CampaignServiceImplTest {

    // Using fixed ids for ease of use
    private final static Long OWNER_ID = 1L;
    private final static Long CAMPAIGN_ID = 2L;
    private final static Long PLAYER_ID = 3L;
    private final static Long OTHER_ID = 99L;

    @Mock
    private CampaignRepository campaignRepository;
    @Mock
    private NoteRepository noteRepository;
    @Mock
    private GameSessionRepository gameSessionRepository;
    @Mock
    private SheetRepository sheetRepository;
    @Mock
    private AppUserService appUserService;

    @InjectMocks
    private CampaignServiceImpl campaignService;

    private AppUser owner;
    private Campaign campaign;

    @BeforeEach
    void setup() {
        // Arrange
        owner = AppUser.builder().userId(OWNER_ID).username("DM").build();
        campaign = Campaign.builder()
                .campaignId(CAMPAIGN_ID)
                .name("Campaign1")
                .description("Desc1")
                .owner(owner)
                .build();
    }

    @Test
    void createCampaign() {
        // Arrange
        when(appUserService.getUser(OWNER_ID)).thenReturn(owner);
        when(campaignRepository.existsByNameAndOwner_UserId("Campaign2", OWNER_ID)).thenReturn(false);
        when(campaignRepository.save(any())).thenAnswer(call -> call.getArgument(0));

        // Act
        Campaign created = campaignService.createCampaign(
                new CreateCampaignRequest("Campaign2", OWNER_ID, "Desc2"));

        // Assert
        assertEquals("Campaign2", created.getName());
        assertEquals(owner, created.getOwner());
    }

    @Test
    void createCampaign_duplicateName() {
        // Arrange
        when(appUserService.getUser(OWNER_ID)).thenReturn(owner);
        when(campaignRepository.existsByNameAndOwner_UserId("Campaign1", OWNER_ID)).thenReturn(true);

        // Act - Assert
        assertThrows(DuplicateCampaignPerUserException.class, () -> campaignService.createCampaign(
                new CreateCampaignRequest("Campaign1", OWNER_ID, "Desc1")));

        verify(campaignRepository, never()).save(any());
    }

    @Test
    void getCampaign_missing() {
        // Arrange
        when(campaignRepository.findById(CAMPAIGN_ID)).thenReturn(Optional.empty());

        // Act - Assert
        assertThrows(CampaignNotFoundException.class, () -> campaignService.getCampaign(CAMPAIGN_ID));
    }

    @Test
    void getAllCampaigns() {
        // Arrange
        when(campaignRepository.findByOwner_UserId(OWNER_ID)).thenReturn(List.of(campaign));

        // Act - Assert
        assertEquals(1, campaignService.getAllCampaigns(OWNER_ID).size());
    }

    @Test
    void deleteCampaign_onlyOwnerAllowed() {
        // Arrange
        when(campaignRepository.findById(CAMPAIGN_ID)).thenReturn(Optional.of(campaign));

        // Act - Assert
        assertThrows(CampaignPermissionException.class,
                () -> campaignService.deleteCampaign(CAMPAIGN_ID, OTHER_ID));

        verify(campaignRepository, never()).delete(any()); // Verify that nothing is deleted
    }

    @Test
    void updateCampaign() {
        // Arrange
        when(campaignRepository.findById(CAMPAIGN_ID)).thenReturn(Optional.of(campaign));
        when(campaignRepository.save(any())).thenAnswer(call -> call.getArgument(0));

        // Act
        Campaign updated = campaignService.updateCampaign(
                new UpdateCampaignRequest(CAMPAIGN_ID, "Campaign1", "New desc"), OWNER_ID);

        // Assert
        assertEquals("New desc", updated.getDescription());
        verify(campaignRepository, never()).existsByNameAndOwner_UserId(any(), any());
    }

    @Test
    void updateCampaign_duplicateName() {
        // Arrange
        when(campaignRepository.findById(CAMPAIGN_ID)).thenReturn(Optional.of(campaign));
        when(campaignRepository.existsByNameAndOwner_UserId("Duplicate", OWNER_ID)).thenReturn(true);

        // Act - Assert
        assertThrows(DuplicateCampaignPerUserException.class, () -> campaignService.updateCampaign(
                new UpdateCampaignRequest(CAMPAIGN_ID, "Duplicate", "Desc"), OWNER_ID));
    }

    @Test
    void addPlayer() {
        // Arrange
        when(campaignRepository.findById(CAMPAIGN_ID)).thenReturn(Optional.of(campaign));
        when(campaignRepository.save(any())).thenAnswer(call -> call.getArgument(0));

        // Act
        Campaign updated = campaignService.addPlayer(
                new AddPlayerRequest(CAMPAIGN_ID, "Omega"), OWNER_ID);

        // Assert
        assertEquals(1, updated.getPlayers().size());
        assertEquals(campaign, updated.getPlayers().getFirst().getCampaign());
    }

    @Test
    void addPlayer_duplicateName() {
        // Arrange
        campaign.addPlayer(player("Omega"));
        when(campaignRepository.findById(CAMPAIGN_ID)).thenReturn(Optional.of(campaign));

        // Act - Assert
        assertThrows(DuplicatePlayerException.class, () -> campaignService.addPlayer(
                new AddPlayerRequest(CAMPAIGN_ID, "omega"), OWNER_ID));
    }

    @Test
    void renamePlayer() {
        // Arrange
        Player existing = player("Omega");
        campaign.addPlayer(existing);
        when(campaignRepository.findById(CAMPAIGN_ID)).thenReturn(Optional.of(campaign));
        when(campaignRepository.save(any())).thenAnswer(call -> call.getArgument(0));

        // Act
        campaignService.renamePlayer(
                new RenamePlayerRequest(CAMPAIGN_ID, PLAYER_ID, "Alpha"), OWNER_ID);

        // Assert
        assertEquals("Alpha", existing.getName());
        assertEquals(PLAYER_ID, existing.getPlayerId());
    }

    @Test
    void renamePlayer_sameNameDifferentCase() {
        // Arrange
        Player existing = player("Omega");
        campaign.addPlayer(existing);
        when(campaignRepository.findById(CAMPAIGN_ID)).thenReturn(Optional.of(campaign));
        when(campaignRepository.save(any())).thenAnswer(call -> call.getArgument(0));

        // Act
        campaignService.renamePlayer(
                new RenamePlayerRequest(CAMPAIGN_ID, PLAYER_ID, "OMEGA"), OWNER_ID);

        // Assert
        assertEquals("OMEGA", existing.getName());
    }

    @Test
    void renamePlayer_duplicateName() {
        // Arrange
        campaign.addPlayer(player("Omega"));
        campaign.addPlayer(Player.builder().playerId(8L).name("BowserJr")
                .normalizedName("bowserjr").campaign(campaign).build());

        when(campaignRepository.findById(CAMPAIGN_ID)).thenReturn(Optional.of(campaign));

        // Act - Assert
        assertThrows(DuplicatePlayerException.class, () -> campaignService.renamePlayer(
                new RenamePlayerRequest(CAMPAIGN_ID, PLAYER_ID, "BowserJr"), OWNER_ID));
    }

    @Test
    void renamePlayer_missing() {
        // Arrange
        when(campaignRepository.findById(CAMPAIGN_ID)).thenReturn(Optional.of(campaign));

        // Act - Assert
        assertThrows(PlayerNotFoundException.class, () -> campaignService.renamePlayer(
                new RenamePlayerRequest(CAMPAIGN_ID, PLAYER_ID, "BowserJr"), OWNER_ID));
    }

    @Test
    void removePlayer() {
        // Arrange
        campaign.addPlayer(player("Omega"));
        when(campaignRepository.findById(CAMPAIGN_ID)).thenReturn(Optional.of(campaign));
        when(campaignRepository.save(any())).thenAnswer(call -> call.getArgument(0));

        // Act
        Campaign updated = campaignService.removePlayer(CAMPAIGN_ID, PLAYER_ID, OWNER_ID);

        // Assert
        assertTrue(updated.getPlayers().isEmpty());
    }

    @Test
    void exportCampaign() {
        // Arrange
        when(campaignRepository.findById(CAMPAIGN_ID)).thenReturn(Optional.of(campaign));
        when(noteRepository.findByCampaign_CampaignIdOrderByCreatedAsc(CAMPAIGN_ID))
                .thenReturn(List.of(Note.builder().noteId(1L).text("note").build()));
        when(gameSessionRepository.findByCampaign_CampaignIdOrderByStartDateAsc(CAMPAIGN_ID))
                .thenReturn(List.of());
        when(sheetRepository.findByCampaign_CampaignIdOrderByNameAsc(CAMPAIGN_ID))
                .thenReturn(List.of());

        // Act
        CampaignExport export = campaignService.exportCampaign(CAMPAIGN_ID, OWNER_ID);

        // Assert
        assertEquals(campaign, export.campaign());
        assertEquals(1, export.notes().size());
    }

    @Test
    void exportCampaign_onlyOwnerAllowed() {
        // Arrange
        when(campaignRepository.findById(CAMPAIGN_ID)).thenReturn(Optional.of(campaign));

        // Act - Assert
        assertThrows(CampaignPermissionException.class,
                () -> campaignService.exportCampaign(CAMPAIGN_ID, OTHER_ID));

        verifyNoInteractions(noteRepository, gameSessionRepository, sheetRepository);
    }


    // ----- HELPERS -----

    private Player player(String name) {
        return Player.builder()
                .playerId(PLAYER_ID)
                .name(name)
                .normalizedName(Player.normalize(name))
                .campaign(campaign)
                .build();
    }
}