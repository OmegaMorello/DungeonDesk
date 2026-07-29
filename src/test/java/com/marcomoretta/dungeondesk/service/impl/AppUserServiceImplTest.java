package com.marcomoretta.dungeondesk.service.impl;

import com.marcomoretta.dungeondesk.auth.SecretHasher;
import com.marcomoretta.dungeondesk.domain.entity.AppUser;
import com.marcomoretta.dungeondesk.domain.request.CreateAppUserRequest;
import com.marcomoretta.dungeondesk.repository.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

    @Captor
    ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);

    @Test
    void getAllUsers() {
        // Arrange
        AppUser firstUser = AppUser.builder()
                .username("first")
                .hashSecret("first-secret")
                .build();
        AppUser secondUser = AppUser.builder()
                .username("second")
                .hashSecret("second-secret")
                .build();
        when(appUserRepository.findAll(any(Sort.class))).thenReturn(List.of(firstUser, secondUser));

        // Act
        List<AppUser> appUserList = appUserServiceImpl.getAllUsers();

        // Assert
        assertEquals(2, appUserList.size());
        assertEquals(firstUser.getUsername(), appUserList.getFirst().getUsername());
        assertEquals(secondUser.getUsername(), appUserList.getLast().getUsername());
        verify(appUserRepository).findAll(sortCaptor.capture());
        assertEquals(Sort.by(Sort.Direction.ASC, "name"), sortCaptor.getValue());
    }

    @Test
    void getAllUsersWithEmptyDatabase() {
        // Arrange
        when(appUserRepository.findAll(any(Sort.class))).thenReturn(List.of());

        // Act
        List<AppUser> appUserList = appUserServiceImpl.getAllUsers();

        // Assert
        assertNotNull(appUserList);
        assertTrue(appUserList.isEmpty());
    }

    @Test
    void createUser() {
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