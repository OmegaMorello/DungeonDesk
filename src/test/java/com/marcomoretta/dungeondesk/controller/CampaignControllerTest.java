package com.marcomoretta.dungeondesk.controller;

import com.marcomoretta.dungeondesk.auth.AuthSession;
import com.marcomoretta.dungeondesk.auth.LoginType;
import com.marcomoretta.dungeondesk.domain.dto.CampaignDto;
import com.marcomoretta.dungeondesk.domain.dto.request.CreateCampaignRequestDto;
import com.marcomoretta.dungeondesk.domain.dto.request.RenamePlayerRequestDto;
import com.marcomoretta.dungeondesk.domain.entity.AppUser;
import com.marcomoretta.dungeondesk.domain.entity.Campaign;
import com.marcomoretta.dungeondesk.domain.request.CreateCampaignRequest;
import com.marcomoretta.dungeondesk.domain.request.RenamePlayerRequest;
import com.marcomoretta.dungeondesk.exception.ResourcePermissionException;
import com.marcomoretta.dungeondesk.mapper.CampaignMapper;
import com.marcomoretta.dungeondesk.service.CampaignService;
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
class CampaignControllerTest {

    private final static Long OWNER_ID = 1L;
    private final static Long CAMPAIGN_ID = 2L;
    private final static Long PLAYER_ID = 3L;

    @Mock
    private CampaignService campaignService;
    @Mock
    private CampaignMapper campaignMapper;

    @InjectMocks
    private CampaignController controller;

    private Campaign campaign;
    private CampaignDto campaignDto;

    private AuthSession masterSession;
    private AuthSession playerSession;

    @BeforeEach
    void setup() {
        // Arrange
        AppUser owner = AppUser.builder().userId(OWNER_ID).username("DM").build();
        campaign = Campaign.builder().campaignId(CAMPAIGN_ID).name("Campaign1").owner(owner).build();
        campaignDto = new CampaignDto(CAMPAIGN_ID, "Campaign1", "Desc1", OWNER_ID, "DM", List.of());

        masterSession = AuthSession.builder()
                .loginType(LoginType.MASTER).userId(OWNER_ID).displayName("DM").build();

        playerSession = AuthSession.builder()
                .loginType(LoginType.PLAYER).playerId(PLAYER_ID)
                .campaignId(CAMPAIGN_ID).displayName("Omega").build();
    }

    @Test
    void getCampaignList() {
        // Arrange
        when(campaignService.getAllCampaigns(OWNER_ID)).thenReturn(List.of(campaign));
        when(campaignMapper.toDtoList(List.of(campaign))).thenReturn(List.of(campaignDto));

        // Act
        ResponseEntity<List<CampaignDto>> response = controller.getCampaignList(masterSession);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getCampaignList_onlyForMaster() {
        // Act - Assert
        assertThrows(ResourcePermissionException.class,
                () -> controller.getCampaignList(playerSession));

        verifyNoInteractions(campaignService);
    }

    @Test
    void getCampaign() {
        // Arrange
        when(campaignService.getCampaign(CAMPAIGN_ID)).thenReturn(campaign);
        when(campaignMapper.toDto(campaign)).thenReturn(campaignDto);

        // Act - Assert
        assertEquals(HttpStatus.OK, controller.getCampaign(CAMPAIGN_ID).getStatusCode());
    }

    @Test
    void createCampaign_returnsCreated() {
        // Arrange
        CreateCampaignRequestDto dto = new CreateCampaignRequestDto("Campaign1", "Desc1");
        CreateCampaignRequest request = new CreateCampaignRequest("Campaign1", OWNER_ID, "Desc1");

        when(campaignMapper.fromCreateDto(dto, OWNER_ID)).thenReturn(request);
        when(campaignService.createCampaign(request)).thenReturn(campaign);
        when(campaignMapper.toDto(campaign)).thenReturn(campaignDto);

        // Act - Assert
        assertEquals(HttpStatus.CREATED, controller.createCampaign(dto, masterSession).getStatusCode());
    }

    @Test
    void deleteCampaign() {
        // Act - Assert
        assertEquals(HttpStatus.NO_CONTENT,
                controller.deleteCampaign(CAMPAIGN_ID, masterSession).getStatusCode());

        verify(campaignService).deleteCampaign(CAMPAIGN_ID, OWNER_ID);
    }

    @Test
    void renamePlayer() {
        // Arrange
        RenamePlayerRequestDto dto = new RenamePlayerRequestDto("Alpha");
        RenamePlayerRequest request = new RenamePlayerRequest(CAMPAIGN_ID, PLAYER_ID, "Alpha");

        when(campaignMapper.fromRenamePlayerDto(dto, CAMPAIGN_ID, PLAYER_ID)).thenReturn(request);
        when(campaignService.renamePlayer(request, OWNER_ID)).thenReturn(campaign);
        when(campaignMapper.toDto(campaign)).thenReturn(campaignDto);

        // Act - Assert
        assertEquals(HttpStatus.OK,
                controller.renamePlayer(CAMPAIGN_ID, PLAYER_ID, dto, masterSession).getStatusCode());
    }

    @Test
    void removePlayer() {
        // Arrange
        when(campaignService.removePlayer(CAMPAIGN_ID, PLAYER_ID, OWNER_ID)).thenReturn(campaign);
        when(campaignMapper.toDto(campaign)).thenReturn(campaignDto);

        // Act - Assert
        assertEquals(HttpStatus.OK,
                controller.removePlayer(CAMPAIGN_ID, PLAYER_ID, masterSession).getStatusCode());
    }
}