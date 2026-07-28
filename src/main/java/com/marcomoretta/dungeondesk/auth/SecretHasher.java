package com.marcomoretta.dungeondesk.auth;

public interface SecretHasher {
    String hash(String rawSecret);
    boolean matches(String rawSecret, String hashedSecret);
}
