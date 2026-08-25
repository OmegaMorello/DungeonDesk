package com.marcomoretta.dungeondesk.mapper.impl;

import com.marcomoretta.dungeondesk.auth.AuthSession;
import com.marcomoretta.dungeondesk.domain.dto.AuthSessionDto;
import com.marcomoretta.dungeondesk.mapper.AuthMapper;
import org.springframework.stereotype.Component;

/**
 * Mapper for authentication DTOs
 */
@Component
public class AuthMapperImpl implements AuthMapper {
    @Override
    public AuthSessionDto toDto(AuthSession authSession) {
        return new AuthSessionDto(
                authSession.loginType(),
                authSession.displayName(),
                authSession.campaignId());
    }

}
