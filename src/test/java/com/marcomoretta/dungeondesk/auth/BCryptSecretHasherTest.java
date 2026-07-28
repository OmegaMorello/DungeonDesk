package com.marcomoretta.dungeondesk.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class BCryptSecretHasherTest {

    private final BCryptSecretHasher secretHasher = new BCryptSecretHasher();

    @Test
    void hash() {
        // Arrange
        String rawSecret = "secret";

        // Act
        String hashedSecret = secretHasher.hash(rawSecret);

        // Assert
        assertFalse(hashedSecret.isBlank());
        assertNotEquals(rawSecret, hashedSecret);


    }

    /**
     * Testing of 3 different scenarios which should throw an IllegalArgumentException
     * @param invalidSecret will be firstly null, then "" and then "   " (whitespaces)
     */
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "    "})
    void invalidHash(String invalidSecret) {
        // Assert
        assertThrowsExactly(
                IllegalArgumentException.class,
                () -> secretHasher.hash(invalidSecret));
    }

    @Test
    void matches() {
        // Arrange
        String rawSecret = "secret";
        String hashedSecret = secretHasher.hash(rawSecret);

        // Act
        boolean checkSecret = secretHasher.matches(rawSecret, hashedSecret);
        boolean checkEmptySecret = secretHasher.matches(rawSecret, null); // covers all branches [if (x || y)]

        // Assert
        assertTrue(checkSecret);
        assertFalse(checkEmptySecret);
    }
}