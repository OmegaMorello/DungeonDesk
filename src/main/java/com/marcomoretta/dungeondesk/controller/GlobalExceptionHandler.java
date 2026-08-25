package com.marcomoretta.dungeondesk.controller;

import com.marcomoretta.dungeondesk.domain.dto.ErrorDto;
import com.marcomoretta.dungeondesk.exception.*;
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
     *
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
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles the attempt to create a user with an already existing username.
     *
     * @param ex the exception raised by the service
     * @return the error message and a 409 - CONFLICT
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorDto> handleDuplicateResource(DuplicateResourceException ex) {
        ErrorDto errorDto = new ErrorDto(ex.getMessage());
        return new ResponseEntity<>(errorDto, HttpStatus.CONFLICT);
    }

    /**
     * Handles a request for an entity that does not exist
     *
     * @param ex the exception raised by the service
     * @return the error message and a 404 - NOT FOUND
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDto> handleResourceNotFound(ResourceNotFoundException ex) {
        ErrorDto errorDto = new ErrorDto(ex.getMessage());
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles a request a caller has no permission for
     *
     * @param ex the exception raised by the service
     * @return the error message and a 403 - FORBIDDEN
     */
    @ExceptionHandler(ResourcePermissionException.class)
    public ResponseEntity<ErrorDto> handleResourcePermission(ResourcePermissionException ex) {
        ErrorDto errorDto = new ErrorDto(ex.getMessage());
        return new ResponseEntity<>(errorDto, HttpStatus.FORBIDDEN);
    }

    /**
     * Handles a login attempt with wrong credentials
     *
     * @param ex the exception raised by the authentication strategy
     * @return the error message and a 401 - NOT AUTHORIZED
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorDto> handleInvalidCredentials(InvalidCredentialsException ex) {
        ErrorDto errorDto = new ErrorDto(ex.getMessage());
        return new ResponseEntity<>(errorDto, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles a request without a valid session cookie
     *
     * @param ex the exception raised by the service
     * @return the error message and a 401 - NOT AUTHORIZED
     */
    @ExceptionHandler(UnauthenticatedException.class)
    public ResponseEntity<ErrorDto> handleUnauthenticated(UnauthenticatedException ex) {
        ErrorDto errorDto = new ErrorDto(ex.getMessage());
        return new ResponseEntity<>(errorDto, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles the upload of an unaccepted file type
     *
     * @param ex the exception raised by the service
     * @return the error message and a 400 - BAD REQUEST
     */
    @ExceptionHandler(MapImageException.class)
    public ResponseEntity<ErrorDto> handleMapImage(MapImageException ex) {
        ErrorDto errorDto = new ErrorDto(ex.getMessage());
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    /**
     * Fallback for database constraint violations that escaped the application checks
     * (e.g. two concurrent requests creating the same username).
     *
     * @param ex The exception raised by the persistence layer
     * @return Ex conflict message and a 409 Conflict
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorDto> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        ErrorDto errorDto = new ErrorDto(ex.getMessage());
        return new ResponseEntity<>(errorDto, HttpStatus.CONFLICT);
    }
}
