package com.marcomoretta.dungeondesk.auth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;


/**
 * Class that uses the BCrypt security package to hash and check passwords
 */
@Component
public class BCryptSecretHasher implements SecretHasher{

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * Hashes the secret with the BCrypt encoder
     * @param rawSecret the raw secret input by the user
     * @return the encoded(hashed) secret
     */
    @Override
    public String hash(String rawSecret)  {
        if (isBlank(rawSecret))
            throw new IllegalArgumentException("Secret cannot be blank or empty");
        return encoder.encode(rawSecret);
    }

    /**
     * Checks that the raw and hashed secrets match
     * @param rawSecret the secret to be checked
     * @param hashedSecret the hashed secret to check with
     * @return true if the "digests" are the same, false otherwise
     */
    @Override
    public boolean matches(String rawSecret, String hashedSecret) {
        if (isBlank(rawSecret) || isBlank(hashedSecret)) return false;
        return encoder.matches(rawSecret, hashedSecret);
    }

    private boolean isBlank(String secret) {
        return secret == null || secret.isBlank();
    }
}
