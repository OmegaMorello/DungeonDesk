package com.marcomoretta.dungeondesk.mapper.impl;

import com.marcomoretta.dungeondesk.domain.dto.MapStateDto;
import com.marcomoretta.dungeondesk.domain.dto.TokenDto;
import com.marcomoretta.dungeondesk.domain.entity.MapState;
import com.marcomoretta.dungeondesk.domain.entity.Token;
import com.marcomoretta.dungeondesk.mapper.MapStateMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper for the map and tokens to dto
 */
@Component
public class MapStateMapperImpl implements MapStateMapper {
    @Override
    public MapStateDto toDto(MapState mapState) {
        return new MapStateDto(
                mapState.getMapStateId(),
                mapState.getGridRows(),
                mapState.getGridColumns(),
                mapState.getBackgroundUrl() != null && !mapState.getBackgroundUrl().isBlank(),
                toDtoList(mapState.getTokenList()));
    }

    @Override
    public TokenDto toDto(Token token) {
        return new TokenDto(
                token.getTokenId(),
                token.getSheet().getSheetId(),
                token.getSheet().getName(),
                token.getType(),
                token.getPosX(),
                token.getPosY()
        );
    }

    @Override
    public List<TokenDto> toDtoList(List<Token> tokenList) {
        return tokenList.stream().map(this::toDto).toList();
    }
}
