package com.marcomoretta.dungeondesk.mapper.impl;

import com.marcomoretta.dungeondesk.domain.dto.GameSessionDto;
import com.marcomoretta.dungeondesk.domain.dto.SessionPlayerDto;
import com.marcomoretta.dungeondesk.domain.dto.request.CreateGameSessionRequestDto;
import com.marcomoretta.dungeondesk.domain.dto.request.UpdateGameSessionRequestDto;
import com.marcomoretta.dungeondesk.domain.entity.GameSession;
import com.marcomoretta.dungeondesk.domain.entity.Player;
import com.marcomoretta.dungeondesk.domain.request.CreateGameSessionRequest;
import com.marcomoretta.dungeondesk.domain.request.UpdateGameSessionRequest;
import com.marcomoretta.dungeondesk.mapper.GameSessionMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper for the GameSession to and from Dto
 */
@Component
public class GameSessionMapperImpl implements GameSessionMapper {

    @Override
    public CreateGameSessionRequest fromCreateDto(CreateGameSessionRequestDto dto, Long campaignId) {
        return new CreateGameSessionRequest(
                campaignId,
                dto.joinCode()
        );
    }

    @Override
    public UpdateGameSessionRequest fromUpdateDto(UpdateGameSessionRequestDto dto, Long sessionId) {
        return new UpdateGameSessionRequest(
                sessionId,
                dto.joinCode()
        );
    }

    @Override
    public GameSessionDto toDto(GameSession gameSession) {
        return new GameSessionDto(
                gameSession.getSessionId(),
                gameSession.getCampaign().getCampaignId(),
                gameSession.getJoinCode(),
                gameSession.getStartDate(),
                gameSession.getEndDate()
        );
    }

    @Override
    public List<GameSessionDto> toDtoList(List<GameSession> gameSessionList) {
        return gameSessionList.stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<SessionPlayerDto> toSessionPlayerDtoList(List<Player> playerList) {
        return playerList.stream()
                .map(player -> new SessionPlayerDto(player.getName()))
                .toList();
    }
}
