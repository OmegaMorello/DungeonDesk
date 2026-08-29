package com.marcomoretta.dungeondesk.repository;

import com.marcomoretta.dungeondesk.domain.entity.Token;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Token persistency layer interface
 */
@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    @Override
    @EntityGraph(attributePaths = "sheet")
    Optional<Token> findById(Long tokenId);

    boolean existsBySheet_SheetId(Long sheetId);
}
