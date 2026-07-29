package com.marcomoretta.dungeondesk.repository;

import com.marcomoretta.dungeondesk.domain.entity.AppUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class AppUserRepositoryTest {

    @Autowired
    AppUserRepository appUserRepository;

    @Test
    void saveAndRetrieveUser(){
        // Arrange
        String user = "Omega";
        String secret = "test";
        AppUser appUser = AppUser.builder()
                .username(user)
                .hashSecret(secret)
                .build();

        // Act
        AppUser savedUser = appUserRepository.save(appUser);
        Optional<AppUser> reqUser = appUserRepository.findById(savedUser.getUserId());

        //Assert
        assertTrue(reqUser.isPresent());
        assertEquals(user, reqUser.get().getUsername());
    }

}