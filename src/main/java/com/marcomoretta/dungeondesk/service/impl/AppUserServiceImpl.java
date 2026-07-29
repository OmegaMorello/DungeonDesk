package com.marcomoretta.dungeondesk.service.impl;

import com.marcomoretta.dungeondesk.auth.SecretHasher;
import com.marcomoretta.dungeondesk.domain.entity.AppUser;
import com.marcomoretta.dungeondesk.domain.request.CreateAppUserRequest;
import com.marcomoretta.dungeondesk.domain.request.UpdateAppUserRequest;
import com.marcomoretta.dungeondesk.repository.AppUserRepository;
import com.marcomoretta.dungeondesk.service.AppUserService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public AppUser createUser(CreateAppUserRequest request) {

        AppUser user = AppUser.builder()
                .username(request.username())
                .hashSecret(secretHasher.hash(request.secret()))
                .build();

        return appUserRepository.save(user);
    }

    @Override
    public List<AppUser> getAllUsers() {
        return appUserRepository.findAll(BY_USERNAME_ASC);
    }

    //TODO: Implement update user method
    @Override
    public AppUser updateUser(UpdateAppUserRequest request) {
        return null;
    }
}
