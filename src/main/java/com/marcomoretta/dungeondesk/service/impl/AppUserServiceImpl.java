package com.marcomoretta.dungeondesk.service.impl;

import com.marcomoretta.dungeondesk.auth.SecretHasher;
import com.marcomoretta.dungeondesk.domain.entity.AppUser;
import com.marcomoretta.dungeondesk.domain.request.CreateAppUserRequest;
import com.marcomoretta.dungeondesk.domain.request.UpdateAppUserRequest;
import com.marcomoretta.dungeondesk.exception.AppUserNotFoundException;
import com.marcomoretta.dungeondesk.exception.DuplicateUsernameException;
import com.marcomoretta.dungeondesk.exception.InvalidCredentialsException;
import com.marcomoretta.dungeondesk.repository.AppUserRepository;
import com.marcomoretta.dungeondesk.service.AppUserService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serves the AppUser controller
 */
@Service
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository appUserRepository;
    private final SecretHasher secretHasher;
    private final static Sort BY_USERNAME_ASC = Sort.by(Sort.Direction.ASC, "username");

    public AppUserServiceImpl(AppUserRepository appUserRepository, SecretHasher secretHasher) {
        this.appUserRepository = appUserRepository;
        this.secretHasher = secretHasher;
    }

    @Override
    @Transactional
    public AppUser createUser(CreateAppUserRequest request) {

        checkDuplicate(request.username());

        AppUser user = AppUser.builder()
                .username(request.username())
                .hashSecret(secretHasher.hash(request.secret()))
                .build();

        return appUserRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public AppUser getUser(Long userId) {
        return appUserRepository
                .findById(userId)
                .orElseThrow(() -> new AppUserNotFoundException("User not found: " + userId));
    }

    @Override
    @Transactional
    public AppUser updateUser(UpdateAppUserRequest request) {

        AppUser user = getUser(request.id());

        if (!secretHasher.matches(request.currentSecret(), user.getHashSecret()))
            throw new InvalidCredentialsException();

        // Checks duplicate only if the name is changing
        if (!user.getUsername().equals(request.username()))
            checkDuplicate(request.username());

        user.setUsername(request.username());
        user.setHashSecret(secretHasher.hash(request.newSecret()));

        return appUserRepository.save(user);
    }

    private void checkDuplicate(String username) {
        if (appUserRepository.existsByUsername(username))
            throw new DuplicateUsernameException("Username is already taken, please choose a different one");
    }
}
