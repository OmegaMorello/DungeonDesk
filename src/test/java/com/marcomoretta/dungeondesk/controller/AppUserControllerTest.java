package com.marcomoretta.dungeondesk.controller;

import com.marcomoretta.dungeondesk.domain.dto.AppUserDto;
import com.marcomoretta.dungeondesk.domain.dto.CreateAppUserRequestDto;
import com.marcomoretta.dungeondesk.domain.entity.AppUser;
import com.marcomoretta.dungeondesk.domain.request.CreateAppUserRequest;
import com.marcomoretta.dungeondesk.mapper.AppUserMapper;
import com.marcomoretta.dungeondesk.service.AppUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppUserController.class) // Loads a Spring container with only the specified controller class
class AppUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AppUserService appUserService;

    @MockitoBean
    private AppUserMapper appUserMapper;

    @Test
    void listUsers() throws Exception {
        // Arrange
        AppUser appUser1 = AppUser.builder()
                .userId(1L)
                .username("user1")
                .hashSecret("secret1")
                .build();
        AppUser appUser2 = AppUser.builder()
                .userId(2L)
                .username("user2")
                .hashSecret("secret2")
                .build();

        AppUserDto appUserDto1 = new AppUserDto(
                appUser1.getUserId(),
                appUser1.getUsername(),
                null);
        AppUserDto appUserDto2 = new AppUserDto(
                appUser2.getUserId(),
                appUser2.getUsername(),
                null);

        List<AppUser> appUserList = List.of(appUser1, appUser2);
        List<AppUserDto> appUserDtos = List.of(appUserDto1, appUserDto2);

        when(appUserService.getAllUsers()).thenReturn(appUserList);
        when(appUserMapper.toDtoList(appUserList)).thenReturn(appUserDtos);

        // Act and Assert
        mockMvc.perform(get("/api/v1/appusers/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("user1"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].name").value("user2"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void createUser() throws Exception {
        // Arrange
        CreateAppUserRequestDto dto = new CreateAppUserRequestDto("Test", "Secret123");
        CreateAppUserRequest request = new CreateAppUserRequest("Test", "Secret123");
        AppUser appUser = AppUser.builder()
                .userId(1L)
                .username("Test")
                .hashSecret("fake-hash")
                .build();
        AppUserDto appUserDto = new AppUserDto(1L, "Test", List.of());

        // Stubs to simulate the process
        when(appUserMapper.fromDto(dto)).thenReturn(request);
        when(appUserService.createUser(request)).thenReturn(appUser);
        when(appUserMapper.toDto(appUser)).thenReturn(appUserDto);

        // Act and Assert
        mockMvc.perform(post("/api/v1/appusers/create")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test"))
                .andExpect(jsonPath("$.id").value(1))
                .andDo(MockMvcResultHandlers.print());

    }
}