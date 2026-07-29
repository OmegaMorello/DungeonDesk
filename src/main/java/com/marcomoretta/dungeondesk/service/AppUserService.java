package com.marcomoretta.dungeondesk.service;

import com.marcomoretta.dungeondesk.domain.entity.AppUser;
import com.marcomoretta.dungeondesk.domain.request.CreateAppUserRequest;
import com.marcomoretta.dungeondesk.domain.request.UpdateAppUserRequest;

import java.util.List;

/**
 * Interface that defines the App User Service
 */
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

    /**
     * Updates a User
     * @param request Update Request
     * @return The updated user non-sensitive information
     */
    AppUser updateUser(UpdateAppUserRequest request);
}
