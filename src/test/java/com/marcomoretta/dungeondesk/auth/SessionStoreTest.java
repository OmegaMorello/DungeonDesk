package com.marcomoretta.dungeondesk.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SessionStoreTest {

    private final SessionStore sessionStore = new SessionStore();

    @Test
    void createMasterSession() {
        // Act
        AuthSession session = sessionStore.createMasterSession(1L, "DM");

        // Assert
        assertEquals(LoginType.MASTER, session.loginType());
        assertEquals(1L, session.userId());
        assertNotNull(session.token());
        assertNotNull(session.createdAt());
    }

    @Test
    void createPlayerSession() {
        // Act
        AuthSession session = sessionStore.createPlayerSession(3L, 2L, 5L, "Omega");

        // Assert
        assertEquals(LoginType.PLAYER, session.loginType());
        assertEquals(2L, session.campaignId());
        assertEquals(5L, session.sessionId());
        assertNull(session.userId()); // A player has no account
    }

    @Test
    void find() {
        // Arrange
        AuthSession stored = sessionStore.createMasterSession(1L, "DM");

        // Act - Assert
        assertEquals(stored, sessionStore.find(stored.token()).orElseThrow());
        assertTrue(sessionStore.find("not-a-token").isEmpty());
    }

    @Test
    void remove() {
        // Arrange
        AuthSession stored = sessionStore.createMasterSession(1L, "DM");

        // Act
        sessionStore.remove(stored.token());

        // Assert
        assertTrue(sessionStore.find(stored.token()).isEmpty());
    }

    @Test
    void createMasterSession_generatesADifferentTokenEveryTime() {
        // Act
        AuthSession first = sessionStore.createMasterSession(1L, "DM");
        AuthSession second = sessionStore.createMasterSession(1L, "DM");

        // Assert
        assertNotEquals(first.token(), second.token()); // i.e. 2 different browsers
    }
}