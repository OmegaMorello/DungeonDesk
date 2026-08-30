package com.marcomoretta.dungeondesk.controller;

import com.marcomoretta.dungeondesk.domain.dto.ErrorDto;
import com.marcomoretta.dungeondesk.exception.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleResourceNotFound() {
        // Act
        ResponseEntity<ErrorDto> response =
                handler.handleResourceNotFound(new SheetNotFoundException("Sheet not found"));

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Sheet not found", response.getBody().error());
    }

    @Test
    void handleResourcePermission() {
        // Act - Assert - 403: authenticated, but not allowed
        assertEquals(HttpStatus.FORBIDDEN,
                handler.handleResourcePermission(new SheetPermissionException("Nope")).getStatusCode());
    }

    @Test
    void handleDuplicateResource() {
        // Act - Assert
        assertEquals(HttpStatus.CONFLICT,
                handler.handleDuplicateResource(new DuplicatePlayerException("Taken")).getStatusCode());
    }

    @Test
    void handleInvalidCredentials() {
        // Act - Assert
        assertEquals(HttpStatus.UNAUTHORIZED,
                handler.handleInvalidCredentials(new InvalidCredentialsException()).getStatusCode());
    }

    @Test
    void handleUnauthenticated() {
        // Act - Assert
        assertEquals(HttpStatus.UNAUTHORIZED,
                handler.handleUnauthenticated(new UnauthenticatedException()).getStatusCode());
    }

    @Test
    void handleMapImage() {
        // Act - Assert
        assertEquals(HttpStatus.BAD_REQUEST,
                handler.handleMapImage(new MapImageException("Only PNG and JPEG")).getStatusCode());
    }
}