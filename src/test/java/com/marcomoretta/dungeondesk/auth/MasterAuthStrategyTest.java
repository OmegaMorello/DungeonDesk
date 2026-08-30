package com.marcomoretta.dungeondesk.auth;

import com.marcomoretta.dungeondesk.domain.entity.AppUser;
import com.marcomoretta.dungeondesk.exception.InvalidCredentialsException;
import com.marcomoretta.dungeondesk.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MasterAuthStrategyTest {

    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    private SecretHasher secretHasher;
    @Mock
    private SessionStore sessionStore;

    @InjectMocks
    private MasterAuthStrategy masterAuthStrategy;

    private AppUser user;

    @BeforeEach
    void setup() {
        // Arrange
        user = AppUser.builder().userId(1L).username("DM").hashSecret("hashed").build();
    }

    @Test
    void authenticate() {
        // Arrange
        when(appUserRepository.findByUsername("DM")).thenReturn(Optional.of(user));
        when(secretHasher.matches("secret", "hashed")).thenReturn(true);

        // Act
        masterAuthStrategy.authenticate("DM", "secret");

        // Assert
        verify(sessionStore).createMasterSession(1L, "DM");
    }

    @Test
    void authenticate_unknownUsername() {
        // Arrange
        when(appUserRepository.findByUsername("Unown")).thenReturn(Optional.empty());

        // Act - Assert
        assertThrows(InvalidCredentialsException.class,
                () -> masterAuthStrategy.authenticate("Unown", "secret"));

        verifyNoInteractions(secretHasher, sessionStore);
    }

    @Test
    void authenticate_wrongSecret() {
        // Arrange
        when(appUserRepository.findByUsername("DM")).thenReturn(Optional.of(user));
        when(secretHasher.matches("typo", "hashed")).thenReturn(false);

        // Act - Assert - the same exception as the unknown username, on purpose
        assertThrows(InvalidCredentialsException.class,
                () -> masterAuthStrategy.authenticate("DM", "typo"));

        verifyNoInteractions(sessionStore);
    }

    @Test
    void supports() {
        // Act - Assert
        assertEquals(LoginType.MASTER, masterAuthStrategy.supports());
    }
}