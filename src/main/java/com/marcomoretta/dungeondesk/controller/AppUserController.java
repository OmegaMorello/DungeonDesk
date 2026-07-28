package com.marcomoretta.dungeondesk.controller;

import com.marcomoretta.dungeondesk.mapper.AppUserMapper;
import com.marcomoretta.dungeondesk.domain.request.CreateAppUserRequest;
import com.marcomoretta.dungeondesk.domain.dto.AppUserDto;
import com.marcomoretta.dungeondesk.domain.dto.CreateAppUserRequestDto;
import com.marcomoretta.dungeondesk.domain.entity.AppUser;
import com.marcomoretta.dungeondesk.service.AppUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/appusers")
public class AppUserController {

    private final AppUserService appUserService;
    private final AppUserMapper appUserMapper;

    public AppUserController(AppUserService appUserService, AppUserMapper appUserMapper) {
        this.appUserService = appUserService;
        this.appUserMapper = appUserMapper;
    }

    @GetMapping("/all")
    public ResponseEntity<List<AppUserDto>> listUsers() {
        List<AppUser> appUserList = appUserService.getAllUsers();
        List<AppUserDto> appUserDtos = appUserMapper.toDtoList(appUserList);
        return ResponseEntity.ok(appUserDtos);
    }

    @PostMapping("/create")
    public ResponseEntity<AppUserDto> createUser(
            @Valid @RequestBody CreateAppUserRequestDto createAppUserRequestDto
    ) {
        CreateAppUserRequest createAppUserRequest = appUserMapper.fromDto(createAppUserRequestDto);
        AppUser appUser = appUserService.createUser(createAppUserRequest);
        AppUserDto createdAppUserDto = appUserMapper.toDto(appUser);
        return new ResponseEntity<>(createdAppUserDto, HttpStatus.CREATED); //TODO: Check warning
    }
}
