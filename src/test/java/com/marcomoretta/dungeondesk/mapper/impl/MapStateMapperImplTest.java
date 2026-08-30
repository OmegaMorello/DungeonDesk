package com.marcomoretta.dungeondesk.mapper.impl;

import com.marcomoretta.dungeondesk.domain.dto.MapStateDto;
import com.marcomoretta.dungeondesk.domain.entity.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MapStateMapperImplTest {

    private final MapStateMapperImpl mapStateMapper = new MapStateMapperImpl();

    // Arrange
    private final MapState mapState = MapState.builder()
            .gridRows(6)
            .gridColumns(12)
            .campaign(Campaign.builder()
                    .name("TestCampaign")
                    .description("CampaignDesc")
                    .owner(AppUser.builder().build())
                    .players(List.of(Player.builder().name("P1").build(),
                            Player.builder().name("P2").build()))
                    .build())
            .build();

    private final Token token1 = Token.builder()
            .mapState(mapState)
            .posX(5)
            .posY(2)
            .type(TokenType.PC)
            .sheet(CharacterSheet.builder().build())
            .build();

    private final Token token2 = Token.builder()
            .mapState(mapState)
            .posX(2)
            .posY(5)
            .type(TokenType.NPC)
            .sheet(EnemySheet.builder().build())
            .build();


    @Test
    void toDto() {
        // Arrange
        mapState.addToken(token1);
        mapState.addToken(token2);

        // Act
        MapStateDto mapStateDto = mapStateMapper.toDto(mapState);

        // Assert
        assertFalse(mapStateDto.hasBackground());
        assertEquals(2, mapStateDto.tokenList().getLast().posX());
        assertEquals(TokenType.PC, mapStateDto.tokenList().getFirst().tokenType());
        assertEquals(2, mapStateDto.tokenList().size());
    }

    @Test
    void toDto_withBackground() {
        // Arrange
        mapState.setBackgroundUrl("/maps/map1.img");

        // Act
        MapStateDto mapStateDto = mapStateMapper.toDto(mapState);

        // Assert
        assertTrue(mapStateDto.hasBackground());
    }

    @Test
    void toDto_nullBackground() {
        // Arrange
        mapState.setBackgroundUrl(null);

        // Act
        MapStateDto mapStateDto = mapStateMapper.toDto(mapState);

        // Assert
        assertFalse(mapStateDto.hasBackground());
    }
}