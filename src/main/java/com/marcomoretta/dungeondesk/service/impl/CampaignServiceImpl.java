package com.marcomoretta.dungeondesk.service.impl;

import com.marcomoretta.dungeondesk.domain.entity.AppUser;
import com.marcomoretta.dungeondesk.domain.entity.Campaign;
import com.marcomoretta.dungeondesk.domain.entity.Player;
import com.marcomoretta.dungeondesk.domain.request.AddPlayerRequest;
import com.marcomoretta.dungeondesk.domain.request.CreateCampaignRequest;
import com.marcomoretta.dungeondesk.domain.request.UpdateCampaignRequest;
import com.marcomoretta.dungeondesk.exception.*;
import com.marcomoretta.dungeondesk.repository.CampaignRepository;
import com.marcomoretta.dungeondesk.service.AppUserService;
import com.marcomoretta.dungeondesk.service.CampaignService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serves the Campaign controller
 */
@Service
public class CampaignServiceImpl implements CampaignService {

    private final CampaignRepository campaignRepository;
    private final AppUserService appUserService;

    public CampaignServiceImpl(CampaignRepository campaignRepository, AppUserService appUserService) {
        this.campaignRepository = campaignRepository;
        this.appUserService = appUserService;
    }

    @Override
    @Transactional
    public Campaign createCampaign(CreateCampaignRequest request) {

        AppUser appUser = appUserService.getUser(request.ownerId());

        checkDuplicate(request.name(), appUser.getUserId());

        Campaign campaign = Campaign.builder()
                .name(request.name())
                .description(request.description())
                .owner(appUser)
                .build();

        return campaignRepository.save(campaign);
    }

    @Override
    @Transactional(readOnly = true)
    public Campaign getCampaign(Long campaignId) {
        return campaignRepository.findById(campaignId)
                .orElseThrow(() -> new CampaignNotFoundException("Campaign not found: " + campaignId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Campaign> getAllCampaigns(Long ownerId) {
        return campaignRepository.findByOwner_UserId(ownerId);
    }

    @Override
    @Transactional
    public void deleteCampaign(Long campaignId, Long requesterId) {
        Campaign campaign = getCampaign(campaignId);
        checkPermission(campaign, requesterId);
        campaignRepository.delete(campaign);
    }

    //TODO: Missing implementation
    @Override
    public Campaign exportCampaign(Long id) {
        return null;
    }

    @Override
    @Transactional
    public Campaign updateCampaign(UpdateCampaignRequest request, Long requesterId) {
        Campaign campaign = getCampaign(request.id());
        checkPermission(campaign, requesterId);

        if (!campaign.getName().equals(request.name()))
            checkDuplicate(request.name(), campaign.getOwner().getUserId());

        campaign.setName(request.name());
        campaign.setDescription(request.description());

        return campaignRepository.save(campaign);
    }

    @Override
    @Transactional
    public Campaign addPlayer(AddPlayerRequest request, Long requesterId) {
        Campaign campaign = getCampaign(request.campaignId());
        checkPermission(campaign, requesterId);

        String normalizedName = Player.normalize(request.name());

        boolean playerExists = campaign.getPlayers().stream()
                .anyMatch(p -> p.getNormalizedName().equals(normalizedName));

        if (playerExists)
            throw new DuplicatePlayerException("Player name already exists, please choose a different name");

        Player player = Player.builder()
                .name(request.name())
                .campaign(campaign)
                .build();

        campaign.addPlayer(player);

        return campaignRepository.save(campaign);
    }

    @Override
    @Transactional
    public Campaign removePlayer(Long campaignId, Long playerId, Long requesterId) {
        Campaign campaign = getCampaign(campaignId);
        checkPermission(campaign, requesterId);

        Player player = campaign.getPlayers().stream()
                .filter(p -> p.getPlayerId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new PlayerNotFoundException(
                        "Player not found in this campaign: " + playerId));

        campaign.removePlayer(player);

        return campaignRepository.save(campaign);
    }


    private void checkPermission(Campaign campaign, Long userId) {
        if (!campaign.getOwner().getUserId().equals(userId))
            throw new CampaignPermissionException("You do not have the rights to update/delete the campaign: " + campaign.getCampaignId());
    }


    private void checkDuplicate(String name, Long ownerId) {
        if (campaignRepository.existsByNameAndOwner_UserId(name, ownerId))
            throw new DuplicateCampaignPerUserException("Campaign name already exists, please choose a different name");

    }
}
