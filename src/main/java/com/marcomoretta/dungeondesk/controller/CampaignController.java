package com.marcomoretta.dungeondesk.controller;

import com.marcomoretta.dungeondesk.auth.AuthInterceptor;
import com.marcomoretta.dungeondesk.auth.AuthSession;
import com.marcomoretta.dungeondesk.domain.dto.CampaignDto;
import com.marcomoretta.dungeondesk.domain.dto.request.AddPlayerRequestDto;
import com.marcomoretta.dungeondesk.domain.dto.request.CreateCampaignRequestDto;
import com.marcomoretta.dungeondesk.domain.dto.request.UpdateCampaignRequestDto;
import com.marcomoretta.dungeondesk.domain.entity.Campaign;
import com.marcomoretta.dungeondesk.domain.request.AddPlayerRequest;
import com.marcomoretta.dungeondesk.domain.request.CreateCampaignRequest;
import com.marcomoretta.dungeondesk.domain.request.UpdateCampaignRequest;
import com.marcomoretta.dungeondesk.mapper.CampaignMapper;
import com.marcomoretta.dungeondesk.service.CampaignService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * Controller to manage the campaign related requests
 */
@RestController
@RequestMapping("/api/v1/campaigns")
public class CampaignController {

    private final CampaignService campaignService;
    private final CampaignMapper campaignMapper;

    public CampaignController(CampaignService campaignService, CampaignMapper campaignMapper) {
        this.campaignService = campaignService;
        this.campaignMapper = campaignMapper;
    }

    /**
     * Requests a specified campaign by its id
     *
     * @param id The requested campaign id
     * @return Status 200 with the campaign dto
     */
    @GetMapping("/{id}")
    public ResponseEntity<CampaignDto> getCampaign(@PathVariable Long id) {
        return ResponseEntity.ok(campaignMapper.toDto(campaignService.getCampaign(id)));
    }

    /**
     * Request the owner's campaign list
     *
     * @param authSession The active client session
     * @return The campaign list dto [200 - OK]
     */
    @GetMapping
    public ResponseEntity<List<CampaignDto>> getCampaignList(
            @RequestAttribute(AuthInterceptor.SESSION_ATTRIBUTE) AuthSession authSession) {

        authSession.requireMaster();
        List<CampaignDto> campaignDto = campaignMapper.toDtoList(campaignService.getAllCampaigns(authSession.userId()));

        return ResponseEntity.ok(campaignDto);
    }

    /**
     * Request to create a campaign
     *
     * @param createCampaignRequestDto The creation request details
     * @param authSession              The active client session
     * @return The correctly created campaign [201 - CREATED]
     */
    @PostMapping
    public ResponseEntity<CampaignDto> createCampaign(
            @Valid @RequestBody CreateCampaignRequestDto createCampaignRequestDto,
            @RequestAttribute(AuthInterceptor.SESSION_ATTRIBUTE) AuthSession authSession) {

        authSession.requireMaster();
        CreateCampaignRequest createCampaignRequest = campaignMapper.fromCreateDto(createCampaignRequestDto, authSession.userId());
        Campaign campaign = campaignService.createCampaign(createCampaignRequest);
        CampaignDto campaignDto = campaignMapper.toDto(campaign);

        return ResponseEntity
                .created(URI.create("/api/v1/campaigns/" + campaignDto.id()))
                .body(campaignDto); //TODO: Check warning
    }

    /**
     * Request to delete an owned campaign
     *
     * @param campaignId  The id of the campaign to delete
     * @param authSession The active client session
     * @return An empty response in case of successfully deleted campaign [204 - NO CONTENT]
     */
    @DeleteMapping("/{campaignId}")
    public ResponseEntity<Void> deleteCampaign(@PathVariable Long campaignId,
                                               @RequestAttribute(AuthInterceptor.SESSION_ATTRIBUTE) AuthSession authSession) {

        authSession.requireMaster();
        campaignService.deleteCampaign(campaignId, authSession.userId());

        return ResponseEntity.noContent().build();
    }

    /**
     * Request to update an owned campaign
     *
     * @param campaignId               The id of the campaign to update
     * @param updateCampaignRequestDto The update request details
     * @param authSession              The active client session
     * @return The successfully updated campaign [200 - OK]
     */
    @PutMapping("/{campaignId}")
    public ResponseEntity<CampaignDto> updateCampaign(@PathVariable Long campaignId,
                                                      @Valid @RequestBody UpdateCampaignRequestDto updateCampaignRequestDto,
                                                      @RequestAttribute(AuthInterceptor.SESSION_ATTRIBUTE) AuthSession authSession) {

        authSession.requireMaster();
        UpdateCampaignRequest updateCampaignRequest = campaignMapper.fromUpdateDto(updateCampaignRequestDto, campaignId);
        Campaign campaign = campaignService.updateCampaign(updateCampaignRequest, authSession.userId());

        return ResponseEntity.ok(campaignMapper.toDto(campaign));
    }

    /**
     * Request to add a player to a campaign
     *
     * @param campaignId          The id of the campaign to which the player should be added
     * @param addPlayerRequestDto The player details
     * @param authSession         The active client session
     * @return The campaign info with the correctly updated players list
     */
    @PostMapping("/{campaignId}/players")
    public ResponseEntity<CampaignDto> addPlayer(@PathVariable Long campaignId,
                                                 @Valid @RequestBody AddPlayerRequestDto addPlayerRequestDto,
                                                 @RequestAttribute(AuthInterceptor.SESSION_ATTRIBUTE) AuthSession authSession) {

        authSession.requireMaster();
        AddPlayerRequest addPlayerRequest = campaignMapper.fromAddPlayerDto(addPlayerRequestDto, campaignId);
        Campaign campaign = campaignService.addPlayer(addPlayerRequest, authSession.userId());

        return ResponseEntity.ok(campaignMapper.toDto(campaign));
    }

    /**
     * Request to remove a player from a campaign
     *
     * @param campaignId  The id of the campaign from which the player should be removed
     * @param playerId    The player id
     * @param authSession The active client session
     * @return The campaign info with the correctly updated players list
     */
    @DeleteMapping("/{campaignId}/players")
    public ResponseEntity<CampaignDto> removePlayer(@PathVariable Long campaignId,
                                                    @RequestParam Long playerId,
                                                    @RequestAttribute(AuthInterceptor.SESSION_ATTRIBUTE) AuthSession authSession) {

        authSession.requireMaster();
        Campaign campaign = campaignService.removePlayer(campaignId, playerId, authSession.userId());

        return ResponseEntity.ok(campaignMapper.toDto(campaign));
    }

}
