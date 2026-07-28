package com.marcomoretta.dungeondesk.service;

import com.marcomoretta.dungeondesk.domain.request.CreateAppUserRequest;
import com.marcomoretta.dungeondesk.domain.entity.AppUser;

import java.util.List;

public interface AppUserService {
    /**
     * Creates a new user
     * @param request the request with the body to create a new user
     * @return the newly created AppUser or an error which will be raised by the controller
     */
    AppUser createUser(CreateAppUserRequest request);

    /**
     * Gets the list of all users
     * @return a list containing all the non-sensitive users information
     */
    List<AppUser> getAllUsers();
}
