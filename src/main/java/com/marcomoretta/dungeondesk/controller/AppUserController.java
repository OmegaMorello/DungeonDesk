package com.marcomoretta.dungeondesk.controller;

import com.marcomoretta.dungeondesk.auth.AuthInterceptor;
import com.marcomoretta.dungeondesk.auth.AuthSession;
import com.marcomoretta.dungeondesk.domain.dto.AppUserDto;
import com.marcomoretta.dungeondesk.domain.dto.request.UpdateAppUserRequestDto;
import com.marcomoretta.dungeondesk.domain.entity.AppUser;
import com.marcomoretta.dungeondesk.mapper.AppUserMapper;
import com.marcomoretta.dungeondesk.service.AppUserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST API Controller to manage users requests
 */
@RestController
@RequestMapping(path = "/api/v1/appusers")
public class AppUserController {

    private final AppUserService appUserService;
    private final AppUserMapper appUserMapper;

    public AppUserController(AppUserService appUserService, AppUserMapper appUserMapper) {
        this.appUserService = appUserService;
        this.appUserMapper = appUserMapper;
    }

    /**
     * Request a user's name and/or secret update
     *
     * @param authSession             The client active session
     * @param updateAppUserRequestDto The update request details
     * @return The updated user [200 - OK]
     */
    @PutMapping("/update")
    public ResponseEntity<AppUserDto> updateUser(
            @RequestAttribute(AuthInterceptor.SESSION_ATTRIBUTE) AuthSession authSession,
            @RequestBody @Valid UpdateAppUserRequestDto updateAppUserRequestDto) {

        authSession.requireMaster();
        AppUser updatedUser = appUserService.updateUser(appUserMapper.fromUpdateDto(updateAppUserRequestDto, authSession.userId()));

        return ResponseEntity.ok(appUserMapper.toDto(updatedUser));
    }


}
