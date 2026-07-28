package com.marcomoretta.dungeondesk.auth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;


/**
 * Class that uses the BCrypt security package to hash and check passwords
 */
@Component
public class BCryptSecretHasher implements SecretHasher{

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


    @Override
    public String hash(String rawSecret)  {
        if (isBlank(rawSecret))
            throw new IllegalArgumentException("Secret cannot be blank or empty");
        return encoder.encode(rawSecret);
    }


    @Override
    public boolean matches(String rawSecret, String hashedSecret) {
        if (isBlank(rawSecret) || isBlank(hashedSecret)) return false;
        return encoder.matches(rawSecret, hashedSecret);
    }

    private boolean isBlank(String secret) {
        return secret == null || secret.isBlank();
    }
}
