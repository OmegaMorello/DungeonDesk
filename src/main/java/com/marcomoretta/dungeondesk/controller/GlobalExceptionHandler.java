package com.marcomoretta.dungeondesk.controller;

import com.marcomoretta.dungeondesk.domain.dto.ErrorDto;
import com.marcomoretta.dungeondesk.exception.DuplicateResourceException;
import com.marcomoretta.dungeondesk.exception.ResourceNotFoundException;
import com.marcomoretta.dungeondesk.exception.ResourcePermissionException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler for http requests
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * The method takes the MethodArgumentNotValidException and, if exists, gets its error message.
     * It then maps the message on the ErrorDto and wrap it into the returned ResponseEntity
     * @param ex the exception raised
     * @return returns the error message and a 400 bad request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleValidationException(MethodArgumentNotValidException ex) {

        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("Validation Failed.");

        ErrorDto errorDto = new ErrorDto(errorMessage);
        return new ResponseEntity<>(errorDto,HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles the attempt to create a user with an already existing username.
     * @param ex the exception raised by the service
     * @return the error message and a 409 Conflict
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorDto> handleDuplicateResource(DuplicateResourceException ex) {
        ErrorDto errorDto = new ErrorDto(ex.getMessage());
        return new ResponseEntity<>(errorDto, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDto> handleResourceNotFound(ResourceNotFoundException ex) {
        ErrorDto errorDto = new ErrorDto(ex.getMessage());
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourcePermissionException.class)
    public ResponseEntity<ErrorDto> handleResourcePermission(ResourcePermissionException ex) {
        ErrorDto errorDto = new ErrorDto(ex.getMessage());
        return new ResponseEntity<>(errorDto, HttpStatus.FORBIDDEN);
    }

    /**
     * Fallback for database constraint violations that escaped the application checks
     * (e.g. two concurrent requests creating the same username).
     * @param ex the exception raised by the persistence layer
     * @return a generic conflict message and a 409 Conflict
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorDto> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        ErrorDto errorDto = new ErrorDto("The operation conflicts with existing data.");
        return new ResponseEntity<>(errorDto, HttpStatus.CONFLICT);
    }
}
