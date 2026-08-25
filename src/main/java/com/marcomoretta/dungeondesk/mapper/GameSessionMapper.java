package com.marcomoretta.dungeondesk.mapper;

import com.marcomoretta.dungeondesk.domain.dto.GameSessionDto;
import com.marcomoretta.dungeondesk.domain.dto.SessionPlayerDto;
import com.marcomoretta.dungeondesk.domain.dto.request.CreateGameSessionRequestDto;
import com.marcomoretta.dungeondesk.domain.dto.request.UpdateGameSessionRequestDto;
import com.marcomoretta.dungeondesk.domain.entity.GameSession;
import com.marcomoretta.dungeondesk.domain.entity.Player;
import com.marcomoretta.dungeondesk.domain.request.CreateGameSessionRequest;
import com.marcomoretta.dungeondesk.domain.request.UpdateGameSessionRequest;

import java.util.List;

/**
 * Game Session DTO mapper interface
 */
public interface GameSessionMapper {

    /**
     * Maps a session create request from a dto
     *
     * @param dto        Presentation layer request dto
     * @param campaignId The campaign the session belongs to
     * @return The service layer request
     */
    CreateGameSessionRequest fromCreateDto(CreateGameSessionRequestDto dto, Long campaignId);

    /**
     * Maps a session update request from a dto
     *
     * @param dto       Presentation layer request dto
     * @param sessionId The session to update
     * @return The service layer request
     */
    UpdateGameSessionRequest fromUpdateDto(UpdateGameSessionRequestDto dto, Long sessionId);

    /**
     * Maps a session to a dto
     *
     * @param gameSession Service layer session
     * @return Presentation layer session dto
     */
    GameSessionDto toDto(GameSession gameSession);

    List<GameSessionDto> toDtoList(List<GameSession> gameSessionList);

    /**
     * Maps the roster to the public dto
     *
     * @param playerList The players of the running session
     * @return Their display names, without ids or join code
     */
    List<SessionPlayerDto> toSessionPlayerDtoList(List<Player> playerList);
}
