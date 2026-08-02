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
 * Class that exposes REST APIs for users management
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

    @GetMapping("/{id}")
    public ResponseEntity<AppUserDto> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(appUserMapper.toDto(appUserService.getUser(id)));
    }

    /**
     * Gets the complete users list
     *
     * @return users list dto
     */
    @GetMapping
    public ResponseEntity<List<AppUserDto>> listUsers() {
        List<AppUser> appUserList = appUserService.getAllUsers();

        return ResponseEntity.ok(appUserMapper.toDtoList(appUserList));
    }

    /**
     * Creates a new user
     *
     * @param createAppUserRequestDto the dto container with name and secret
     * @return a response 201 CREATED if the user is correctly created
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
