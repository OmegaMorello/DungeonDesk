package com.marcomoretta.dungeondesk.auth;

public interface SecretHasher {
    /**
     * Hashes the secret with the BCrypt encoder
     * @param rawSecret the raw secret input by the user
     * @return the encoded(hashed) secret
     */
    String hash(String rawSecret);

    /**
     * Checks that the raw and hashed secrets match
     * @param rawSecret the secret to be checked
     * @param hashedSecret the hashed secret to check with
     * @return true if the "digests" are the same, false otherwise
     */
    boolean matches(String rawSecret, String hashedSecret);
}
