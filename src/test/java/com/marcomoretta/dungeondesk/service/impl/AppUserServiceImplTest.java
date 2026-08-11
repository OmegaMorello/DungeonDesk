package com.marcomoretta.dungeondesk.service.impl;

import com.marcomoretta.dungeondesk.auth.SecretHasher;
import com.marcomoretta.dungeondesk.domain.entity.AppUser;
import com.marcomoretta.dungeondesk.domain.request.CreateAppUserRequest;
import com.marcomoretta.dungeondesk.exception.DuplicateUsernameException;
import com.marcomoretta.dungeondesk.repository.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppUserServiceImplTest {

    @Mock
    AppUserRepository appUserRepository;

    @Mock
    SecretHasher secretHasher;

    @InjectMocks
    AppUserServiceImpl appUserServiceImpl;

    @Captor
    ArgumentCaptor<AppUser> captor;

    @Test
    void createUser() throws DuplicateUsernameException {
        // Arrange
        String username = "test_user";
        String secret = "test_secret";
        CreateAppUserRequest createAppUserRequest = new CreateAppUserRequest(username, secret);
        when(secretHasher.hash(secret)).thenReturn("fake-hash");

        // Act
        appUserServiceImpl.createUser(createAppUserRequest);

        // Assert
        verify(appUserRepository).save(captor.capture());
        assertEquals(username, captor.getValue().getUsername());
        assertEquals("fake-hash", captor.getValue().getHashSecret());

    }
}