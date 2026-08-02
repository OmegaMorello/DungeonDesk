package com.marcomoretta.dungeondesk.controller;

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
     * Requests a campaign by its id
     *
     * @param id The requested campaign id
     * @return Status 200 with the campaign dto
     */
    @GetMapping("/{id}")
    public ResponseEntity<CampaignDto> getCampaign(@PathVariable Long id) {
        return ResponseEntity.ok(campaignMapper.toDto(campaignService.getCampaign(id)));
    }

    //TODO: Implement Auth session user
    @GetMapping
    public ResponseEntity<List<CampaignDto>> getCampaignList(@RequestParam Long ownerId) {

        List<CampaignDto> campaignDto = campaignMapper.toDtoList(campaignService.getAllCampaigns(ownerId));

        return ResponseEntity.ok(campaignDto);
    }

    //TODO: Implement Auth session user
    @PostMapping
    public ResponseEntity<CampaignDto> createCampaign(@Valid @RequestBody CreateCampaignRequestDto createCampaignRequestDto,
                                                      @RequestParam Long userId) {

        CreateCampaignRequest createCampaignRequest = campaignMapper.fromCreateDto(createCampaignRequestDto, userId);
        Campaign campaign = campaignService.createCampaign(createCampaignRequest);
        CampaignDto campaignDto = campaignMapper.toDto(campaign);

        return ResponseEntity
                .created(URI.create("/api/v1/campaigns/" + campaignDto.id()))
                .body(campaignDto); //TODO: Check warning
    }

    //TODO: Implement Auth session user
    @DeleteMapping("/{campaignId}")
    public ResponseEntity<Void> deleteCampaign(@PathVariable Long campaignId,
                                                     @RequestParam Long requesterId) {
        campaignService.deleteCampaign(campaignId, requesterId);

        return ResponseEntity.noContent().build();
    }

    //TODO: Implement Auth session user
    @PutMapping("/{campaignId}")
    public ResponseEntity<CampaignDto> updateCampaign(@PathVariable Long campaignId,
                                                      @Valid @RequestBody UpdateCampaignRequestDto updateCampaignRequestDto,
                                                      @RequestParam Long requesterId) {
        UpdateCampaignRequest updateCampaignRequest = campaignMapper.fromUpdateDto(updateCampaignRequestDto, campaignId);
        Campaign campaign = campaignService.updateCampaign(updateCampaignRequest, requesterId);

        return ResponseEntity.ok(campaignMapper.toDto(campaign));
    }

    //TODO: Implement Auth session user
    @PostMapping("/{campaignId}/players")
    public ResponseEntity<CampaignDto> addPlayer(@PathVariable Long campaignId,
                                              @Valid @RequestBody AddPlayerRequestDto addPlayerRequestDto,
                                              @RequestParam Long requesterId) {
        AddPlayerRequest addPlayerRequest = campaignMapper.fromAddPlayerDto(addPlayerRequestDto, campaignId);
        Campaign campaign = campaignService.addPlayer(addPlayerRequest, requesterId);

        return ResponseEntity.ok(campaignMapper.toDto(campaign));
    }

    //TODO: Implement Auth session user
    @DeleteMapping("/{campaignId}/players")
    public ResponseEntity<CampaignDto> removePlayer(@PathVariable Long campaignId,
                                                 @RequestParam Long playerId,
                                                 @RequestParam Long requesterId) {
        Campaign campaign = campaignService.removePlayer(campaignId, playerId, requesterId);

        return ResponseEntity.ok(campaignMapper.toDto(campaign));
    }

}
