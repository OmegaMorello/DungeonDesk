package com.marcomoretta.dungeondesk.mapper;

import com.marcomoretta.dungeondesk.auth.AuthSession;
import com.marcomoretta.dungeondesk.domain.dto.AuthSessionDto;
import com.marcomoretta.dungeondesk.domain.dto.SessionInfoDto;

/**
 * Auth DTO mapper interface
 */
public interface AuthMapper {

    /**
     * Maps an auth session to a dto
     *
     * @param authSession Service layer auth session
     * @return Presentation layer auth session dto
     */
    AuthSessionDto toDto(AuthSession authSession);

    SessionInfoDto toSessionInfoDto(AuthSession authSession);
}
