package com.marcomoretta.dungeondesk.service.impl;

import com.marcomoretta.dungeondesk.domain.entity.Campaign;
import com.marcomoretta.dungeondesk.domain.entity.GameSession;
import com.marcomoretta.dungeondesk.domain.entity.Player;
import com.marcomoretta.dungeondesk.domain.request.CreateGameSessionRequest;
import com.marcomoretta.dungeondesk.domain.request.UpdateGameSessionRequest;
import com.marcomoretta.dungeondesk.exception.GameSessionNotFoundException;
import com.marcomoretta.dungeondesk.exception.GameSessionPermissionException;
import com.marcomoretta.dungeondesk.exception.SessionAlreadyOpenException;
import com.marcomoretta.dungeondesk.repository.GameSessionRepository;
import com.marcomoretta.dungeondesk.service.CampaignService;
import com.marcomoretta.dungeondesk.service.GameSessionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Serves the GameSession controller and the public login screen.
 */
@Service
public class GameSessionServiceImpl implements GameSessionService {

    private final GameSessionRepository gameSessionRepository;
    private final CampaignService campaignService;

    public GameSessionServiceImpl(GameSessionRepository gameSessionRepository,
                                  CampaignService campaignService) {
        this.gameSessionRepository = gameSessionRepository;
        this.campaignService = campaignService;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GameSession> getActiveSession() {
        return gameSessionRepository.findByEndDateIsNull();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Player> getActiveSessionRoster() {
        // No open session is not an error here: the login screen shows "no session
        // available" rather than an error banner, so an empty list is ok
        return getActiveSession()
                .map(session -> session.getCampaign().getPlayers())
                .orElseGet(List::of);
    }

    @Override
    @Transactional
    public GameSession createNewSession(CreateGameSessionRequest request, Long requesterId) {

        Campaign campaign = campaignService.getCampaign(request.campaignId());
        checkPermission(campaign, requesterId);

        // Single open session at a time: this is also what lets the join code identify
        // a session on its own, which keeps the two authentication strategies uniform
        getActiveSession().ifPresent(open -> {
            throw new SessionAlreadyOpenException(
                    "A session is already running: close it before opening a new one");
        });

        GameSession gameSession = GameSession.builder()
                .campaign(campaign)
                .joinCode(request.joinCode())
                .build();

        return gameSessionRepository.save(gameSession);
    }

    @Override
    @Transactional
    public GameSession closeSession(Long sessionId, Long requesterId) {

        GameSession gameSession = getSession(sessionId);
        checkPermission(gameSession.getCampaign(), requesterId);

        gameSession.setEndDate(Instant.now());

        return gameSessionRepository.save(gameSession);
    }

    @Override
    @Transactional
    public GameSession updateSession(UpdateGameSessionRequest request, Long requesterId) {

        GameSession gameSession = getSession(request.sessionId());
        checkPermission(gameSession.getCampaign(), requesterId);

        gameSession.setJoinCode(request.joinCode());

        return gameSessionRepository.save(gameSession);
    }

    private GameSession getSession(Long sessionId) {
        return gameSessionRepository.findById(sessionId)
                .orElseThrow(() -> new GameSessionNotFoundException("Session not found: " + sessionId));
    }

    private void checkPermission(Campaign campaign, Long requesterId) {
        if (!campaign.getOwner().getUserId().equals(requesterId))
            throw new GameSessionPermissionException(
                    "Only the owner of the campaign can manage its sessions");
    }
}
