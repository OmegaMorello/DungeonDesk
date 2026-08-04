package com.marcomoretta.dungeondesk.domain.dto.request;

import com.marcomoretta.dungeondesk.auth.LoginType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * The login request from the front-end call
 *
 * @param loginType The login type enum
 * @param username  The username
 * @param secret    The raw secret
 */
public record LoginRequestDto(
        @NotNull(message = NULL_LOGIN_TYPE)
        LoginType loginType,

        @NotBlank(message = EMPTY_USERNAME)
        String username,

        @NotBlank(message = EMPTY_SECRET)
        String secret

) {
    /**
     * Trims (strips) the name during construction
     */
    public LoginRequestDto {
        if (username != null) username = username.strip();
    }

    private static final String NULL_LOGIN_TYPE = "Null login type";
    private static final String EMPTY_USERNAME = "Username cannot be empty";
    private static final String EMPTY_SECRET = "Secret cannot be empty";

}
