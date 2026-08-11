package com.marcomoretta.dungeondesk.service;

import com.marcomoretta.dungeondesk.domain.entity.AppUser;
import com.marcomoretta.dungeondesk.domain.request.CreateAppUserRequest;
import com.marcomoretta.dungeondesk.domain.request.UpdateAppUserRequest;

/**
 * Interface that defines the App User Service
 */
public interface AppUserService {
    /**
     * Creates a new user
     *
     * @param request Request with the body to create a new user
     * @return Newly created AppUser or an error which will be raised by the controller
     */
    AppUser createUser(CreateAppUserRequest request);

    /**
     * Gets an AppUser by its id
     *
     * @param userId The id of the requested user
     * @return The complete AppUser object
     */
    AppUser getUser(Long userId);


    /**
     * Updates a User
     *
     * @param request Update Request
     * @return The updated user non-sensitive information
     */
    AppUser updateUser(UpdateAppUserRequest request);
}
