package com.marcomoretta.dungeondesk.controller;

import com.marcomoretta.dungeondesk.auth.AuthInterceptor;
import com.marcomoretta.dungeondesk.auth.AuthSession;
import com.marcomoretta.dungeondesk.domain.dto.SheetDto;
import com.marcomoretta.dungeondesk.domain.dto.request.SheetRequestDto;
import com.marcomoretta.dungeondesk.domain.entity.GenericSheet;
import com.marcomoretta.dungeondesk.domain.request.SheetRequest;
import com.marcomoretta.dungeondesk.mapper.SheetMapper;
import com.marcomoretta.dungeondesk.service.SheetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * Class that exposes REST APIs for creature sheets management
 * Creation is split by kind, everything else works on the common type
 */
@RestController
@RequestMapping("/api/v1/sheets")
public class SheetController {

    private final SheetService sheetService;
    private final SheetMapper sheetMapper;

    public SheetController(SheetService sheetService, SheetMapper sheetMapper) {
        this.sheetService = sheetService;
        this.sheetMapper = sheetMapper;
    }

    /**
     * Requests the creation of a player character sheet
     *
     * @param dto         The sheet content
     * @param authSession The client active session
     * @return The created sheet [201 - CREATED]
     */
    @PostMapping("/characters")
    public ResponseEntity<SheetDto> createCharacterSheet(
            @Valid @RequestBody SheetRequestDto dto,
            @RequestAttribute(AuthInterceptor.SESSION_ATTRIBUTE) AuthSession authSession) {

        authSession.requireMaster();

        SheetRequest request = sheetMapper.fromDto(dto, null, authSession.userId());
        GenericSheet sheet = sheetService.createCharacterSheet(request, authSession);
        SheetDto created = sheetMapper.toDto(sheet);

        return ResponseEntity
                .created(URI.create("/api/v1/sheets/" + created.sheetId()))
                .body(created);
    }

    /**
     * Requests the creation of an enemy sheet
     *
     * @param dto         The sheet content
     * @param authSession The client active session
     * @return The created sheet [201 - CREATED]
     */
    @PostMapping("/enemies")
    public ResponseEntity<SheetDto> createEnemySheet(
            @Valid @RequestBody SheetRequestDto dto,
            @RequestAttribute(AuthInterceptor.SESSION_ATTRIBUTE) AuthSession authSession) {

        authSession.requireMaster();

        SheetRequest request = sheetMapper.fromDto(dto, null, authSession.userId());
        GenericSheet sheet = sheetService.createEnemySheet(request, authSession);
        SheetDto created = sheetMapper.toDto(sheet);

        return ResponseEntity
                .created(URI.create("/api/v1/sheets/" + created.sheetId()))
                .body(created);
    }

    /**
     * Requests a sheet by its id
     *
     * @param sheetId     The requested sheet
     * @param authSession The client active session
     * @return The sheet [200 - OK]
     */
    @GetMapping("/{sheetId}")
    public ResponseEntity<SheetDto> getSheet(
            @PathVariable Long sheetId,
            @RequestAttribute(AuthInterceptor.SESSION_ATTRIBUTE) AuthSession authSession) {

        return ResponseEntity.ok(sheetMapper.toDto(sheetService.getSheet(sheetId, authSession)));
    }

    /**
     * Requests every sheet in the caller library
     *
     * @param authSession The client active session
     * @return The sheet list [200 - OK]
     */
    @GetMapping
    public ResponseEntity<List<SheetDto>> getOwnedSheets(
            @RequestAttribute(AuthInterceptor.SESSION_ATTRIBUTE) AuthSession authSession) {

        authSession.requireMaster();

        return ResponseEntity.ok(sheetMapper.toDtoList(sheetService.getOwnedSheets(authSession)));
    }

    /**
     * Requests a sheet update by the owner or by the assigned player
     *
     * @param sheetId     The sheet to update
     * @param dto         The updated content
     * @param authSession The client active session
     * @return The updated sheet [200 - OK]
     */
    @PutMapping("/{sheetId}")
    public ResponseEntity<SheetDto> updateSheet(
            @PathVariable Long sheetId,
            @Valid @RequestBody SheetRequestDto dto,
            @RequestAttribute(AuthInterceptor.SESSION_ATTRIBUTE) AuthSession authSession) {

        SheetRequest request = sheetMapper.fromDto(dto, sheetId, authSession.userId());
        GenericSheet sheet = sheetService.updateSheet(request, authSession);

        return ResponseEntity.ok(sheetMapper.toDto(sheet));
    }

    /**
     * Requests a sheet deletion
     *
     * @param sheetId     The sheet to delete
     * @param authSession The client active session
     * @return No content [204 - NO CONTENT]
     */
    @DeleteMapping("/{sheetId}")
    public ResponseEntity<Void> deleteSheet(
            @PathVariable Long sheetId,
            @RequestAttribute(AuthInterceptor.SESSION_ATTRIBUTE) AuthSession authSession) {

        authSession.requireMaster();

        sheetService.deleteSheet(sheetId, authSession);

        return ResponseEntity.noContent().build();
    }
}
