package com.marcomoretta.dungeondesk.auth;

import com.marcomoretta.dungeondesk.domain.entity.Campaign;
import com.marcomoretta.dungeondesk.domain.entity.GameSession;
import com.marcomoretta.dungeondesk.domain.entity.Player;
import com.marcomoretta.dungeondesk.exception.InvalidCredentialsException;
import com.marcomoretta.dungeondesk.repository.GameSessionRepository;
import org.springframework.stereotype.Component;

/**
 * The authentication strategy of the player
 */
@Component
public class PlayerAuthStrategy implements AuthStrategy {

    private final SessionStore sessionStore;
    private final GameSessionRepository gameSessionRepository;

    public PlayerAuthStrategy(SessionStore sessionStore, GameSessionRepository gameSessionRepository) {
        this.sessionStore = sessionStore;
        this.gameSessionRepository = gameSessionRepository;
    }

    /**
     * The player authentication needs to find the game session using the specified secret, which will be a join code
     * The join code is not hashed, because the Dungeon Masters can change it and read it out to the players
     *
     * @param username The username
     * @param secret   The raw secret
     * @return The created Player session
     */
    @Override
    public AuthSession authenticate(String username, String secret) {
        GameSession gameSession = gameSessionRepository
                .findByJoinCodeAndEndDateIsNull(secret) // This works because only 1 session can be open at a time
                .orElseThrow(InvalidCredentialsException::new);

        Campaign campaign = gameSession.getCampaign();

        String normalized = Player.normalize(username);

        Player player = campaign.getPlayers().stream()
                .filter(p -> p.getNormalizedName().equals(normalized))
                .findFirst()
                .orElseThrow(InvalidCredentialsException::new);

        return sessionStore.createPlayerSession(player.getPlayerId(), campaign.getCampaignId(), gameSession.getSessionId(), player.getName());
    }

    @Override
    public LoginType supports() {
        return LoginType.PLAYER;
    }
}
