package com.marcomoretta.dungeondesk.controller;

import com.marcomoretta.dungeondesk.domain.dto.AppUserDto;
import com.marcomoretta.dungeondesk.domain.dto.request.CreateAppUserRequestDto;
import com.marcomoretta.dungeondesk.domain.entity.AppUser;
import com.marcomoretta.dungeondesk.domain.request.CreateAppUserRequest;
import com.marcomoretta.dungeondesk.mapper.AppUserMapper;
import com.marcomoretta.dungeondesk.service.AppUserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

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
     * Requests a specified user by its id
     *
     * @param id The id of the user to find
     * @return The specified user [200 - OK]
     */
    @GetMapping("/{id}")
    public ResponseEntity<AppUserDto> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(appUserMapper.toDto(appUserService.getUser(id)));
    }

    /**
     * Requests the complete users list
     *
     * @return Users list dto [200 - OK]
     */
    @GetMapping
    public ResponseEntity<List<AppUserDto>> listUsers() {
        List<AppUser> appUserList = appUserService.getAllUsers();

        return ResponseEntity.ok(appUserMapper.toDtoList(appUserList));
    }

    /**
     * Requests to create a new user
     *
     * @param createAppUserRequestDto The dto container with name and secret
     * @return The correctly created user [201 - CREATED]
     */
    @PostMapping
    public ResponseEntity<AppUserDto> createUser(
            @Valid @RequestBody CreateAppUserRequestDto createAppUserRequestDto
    ) {
        CreateAppUserRequest createAppUserRequest = appUserMapper.fromCreateDto(createAppUserRequestDto);
        AppUser appUser = appUserService.createUser(createAppUserRequest);
        AppUserDto appUserDto = appUserMapper.toDto(appUser);

        return ResponseEntity
                .created(URI.create("/api/v1/appusers/" + appUserDto.id()))
                .body(appUserDto); //TODO: Check warning
    }
}
