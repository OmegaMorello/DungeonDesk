package com.marcomoretta.dungeondesk.mapper;

import com.marcomoretta.dungeondesk.domain.dto.SheetDto;
import com.marcomoretta.dungeondesk.domain.dto.SheetSummaryDto;
import com.marcomoretta.dungeondesk.domain.dto.request.SheetRequestDto;
import com.marcomoretta.dungeondesk.domain.entity.GenericSheet;
import com.marcomoretta.dungeondesk.domain.request.SheetRequest;

import java.util.List;

/**
 * Sheet DTO mapper interface
 */
public interface SheetMapper {

    /**
     * Maps a sheet create or update request from a dto
     *
     * @param dto     Presentation layer request dto
     * @param sheetId The sheet to update, null when creating
     * @param ownerId The Dungeon Master the sheet belongs to
     * @return The service layer request
     */
    SheetRequest fromDto(SheetRequestDto dto, Long sheetId, Long ownerId);

    /**
     * Maps a sheet to a dto, computing the derived values
     *
     * @param sheet Service layer sheet, either kind
     * @return Presentation layer sheet dto
     */
    SheetDto toDto(GenericSheet sheet);

    /**
     * Maps a sheet list to a dto list
     *
     * @param sheetList Service layer sheet list
     * @return Presentation layer sheet dto list
     */
    List<SheetDto> toDtoList(List<GenericSheet> sheetList);


    /**
     * Maps a sheet summary to a dto
     *
     * @param sheet Service layer sheet, either kind
     * @return Presentation layer sheet dto
     */
    SheetSummaryDto toSummaryDto(GenericSheet sheet);


    /**
     * Maps a sheet summary list to a dto list
     *
     * @param sheetList Service layer sheet summary list
     * @return Presentation layer sheet summary dto list
     */
    List<SheetSummaryDto> toSummaryDtoList(List<GenericSheet> sheetList);
}
