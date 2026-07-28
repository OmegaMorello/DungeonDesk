package com.marcomoretta.dungeondesk.service;

import com.marcomoretta.dungeondesk.domain.request.CreateAppUserRequest;
import com.marcomoretta.dungeondesk.domain.entity.AppUser;

import java.util.List;

public interface AppUserService {
    AppUser createUser(CreateAppUserRequest request);
    List<AppUser> getAllUsers();
}
