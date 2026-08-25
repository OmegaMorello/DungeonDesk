package com.marcomoretta.dungeondesk.mapper;

import com.marcomoretta.dungeondesk.domain.dto.MapStateDto;
import com.marcomoretta.dungeondesk.domain.dto.TokenDto;
import com.marcomoretta.dungeondesk.domain.entity.MapState;
import com.marcomoretta.dungeondesk.domain.entity.Token;

import java.util.List;

/**
 * MapState DTO mapper interface
 */
public interface MapStateMapper {

    /**
     * Maps a MapState to a dto
     *
     * @param mapState Service layer map state
     * @return Presentation layer map state dto
     */
    MapStateDto toDto(MapState mapState);

    /**
     * Maps a token to a dto
     *
     * @param token Service layer token
     * @return Presentation layer token dto
     */
    TokenDto toDto(Token token);

    /**
     * Maps a token list to a dto list
     *
     * @param tokenList Service layer token list
     * @return Presentation layer token list dto
     */
    List<TokenDto> toDtoList(List<Token> tokenList);
}
