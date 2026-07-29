package com.marcomoretta.dungeondesk.controller;

import com.marcomoretta.dungeondesk.domain.dto.AppUserDto;
import com.marcomoretta.dungeondesk.domain.dto.request.CreateAppUserRequestDto;
import com.marcomoretta.dungeondesk.domain.entity.AppUser;
import com.marcomoretta.dungeondesk.domain.request.CreateAppUserRequest;
import com.marcomoretta.dungeondesk.mapper.AppUserMapper;
import com.marcomoretta.dungeondesk.service.AppUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    /**
     * Gets the complete users list
     * @return users list dto
     */
    @GetMapping("/all")
    public ResponseEntity<List<AppUserDto>> listUsers() {
        List<AppUser> appUserList = appUserService.getAllUsers();
        List<AppUserDto> appUserDtos = appUserMapper.toDtoList(appUserList);
        return ResponseEntity.ok(appUserDtos);
    }

    /**
     * Creates a new user
     * @param createAppUserRequestDto the dto container with name and secret
     * @return a response 201 CREATED if the user is correctly created
     */
    @PostMapping("/create")
    public ResponseEntity<AppUserDto> createUser(
            @Valid @RequestBody CreateAppUserRequestDto createAppUserRequestDto
    ) {
        CreateAppUserRequest createAppUserRequest = appUserMapper.fromCreateDto(createAppUserRequestDto);
        AppUser appUser = appUserService.createUser(createAppUserRequest);
        AppUserDto createdAppUserDto = appUserMapper.toDto(appUser);
        return new ResponseEntity<>(createdAppUserDto, HttpStatus.CREATED); //TODO: Check warning
    }
}
