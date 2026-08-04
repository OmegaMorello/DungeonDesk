package com.marcomoretta.dungeondesk.service.impl;

import com.marcomoretta.dungeondesk.auth.AuthSession;
import com.marcomoretta.dungeondesk.auth.AuthStrategy;
import com.marcomoretta.dungeondesk.auth.LoginType;
import com.marcomoretta.dungeondesk.service.AuthService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Serves the Auth controller
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final Map<LoginType, AuthStrategy> strategyMap;

    /**
     * Iterates through the AuthStrategy list passed by Spring and maps each LoginType with each strategy.
     * .toUnmodifiableMap throws an IllegalStateException at start if a duplicate is found
     *
     * @param strategyList The list of beans passed by Spring
     */
    public AuthServiceImpl(List<AuthStrategy> strategyList) {
        this.strategyMap = strategyList.stream().collect(Collectors.toUnmodifiableMap(AuthStrategy::supports, Function.identity()));
    }

    /**
     * Creates the session if the strategy exists
     *
     * @param type Login Type enum
     * @param username The username
     * @param secret The raw secret
     * @return A valid session
     */
    @Override
    public AuthSession login(LoginType type, String username, String secret) {
        AuthStrategy strategy = strategyMap.get(type);
        if (strategy == null)
            throw new IllegalStateException("AuthStrategy could not be found");
        return strategy.authenticate(username, secret);
    }
}
