package com.marcomoretta.dungeondesk.service;

import com.marcomoretta.dungeondesk.auth.AuthSession;
import com.marcomoretta.dungeondesk.auth.LoginType;

/**
 * Interface for the authorization service
 */
public interface AuthService {
    /**
     * Handles the login logic and supports different login types
     *
     * @param type     The login type enum
     * @param username The username
     * @param secret   The raw secret
     * @return A successfully created authentication session based on the login type
     */
    AuthSession login(LoginType type, String username, String secret);
}
