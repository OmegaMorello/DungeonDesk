package com.marcomoretta.dungeondesk.mapper;

import com.marcomoretta.dungeondesk.auth.AuthSession;
import com.marcomoretta.dungeondesk.domain.dto.AuthSessionDto;

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
}
