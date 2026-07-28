package com.marcomoretta.dungeondesk.domain.request;

public record CreateAppUserRequest(
        String name,
        String secret
) {
}
