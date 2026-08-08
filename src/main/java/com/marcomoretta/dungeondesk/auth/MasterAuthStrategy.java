package com.marcomoretta.dungeondesk.auth;

import com.marcomoretta.dungeondesk.domain.entity.AppUser;
import com.marcomoretta.dungeondesk.exception.InvalidCredentialsException;
import com.marcomoretta.dungeondesk.repository.AppUserRepository;
import org.springframework.stereotype.Component;

/**
 * The authentication strategy of the Dungeon Master
 */
@Component
public class MasterAuthStrategy implements AuthStrategy {
    private final SessionStore sessionStore;
    private final AppUserRepository appUserRepository;
    private final SecretHasher secretHasher;

    public MasterAuthStrategy(SessionStore sessionStore, AppUserRepository appUserRepository, SecretHasher secretHasher) {
        this.sessionStore = sessionStore;
        this.appUserRepository = appUserRepository;
        this.secretHasher = secretHasher;
    }


    /**
     * The Dungeon Master authentication is a normal login: looks for the username and checks if the hashed secret matches the saved one
     *
     * @param username The username
     * @param secret   The raw secret
     * @return The created Master session
     */
    @Override
    public AuthSession authenticate(String username, String secret) {
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(InvalidCredentialsException::new);

        if (!secretHasher.matches(secret, user.getHashSecret()))
            throw new InvalidCredentialsException();

        return sessionStore.createMasterSession(user.getUserId(), user.getUsername());
    }

    @Override
    public LoginType supports() {
        return LoginType.MASTER;
    }
}
