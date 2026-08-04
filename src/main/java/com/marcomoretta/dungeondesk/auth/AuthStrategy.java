package com.marcomoretta.dungeondesk.auth;

/**
 * Authentication Strategy interface
 * The strategy can be implemented by differentiating Login Types (one strategy for each login type)
 */
public interface AuthStrategy {
    /**
     * Search for the user and checks credentials before creating a new session
     *
     * @param username The username
     * @param secret   The raw secret
     * @return A newly created session
     */
    AuthSession authenticate(String username, String secret);


    /**
     * Simply returns the LoginType
     *
     * @return LoginType
     */
    LoginType supports();
}
