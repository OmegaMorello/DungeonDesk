package com.marcomoretta.dungeondesk.repository;

import com.marcomoretta.dungeondesk.domain.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Token persistency layer interface
 */
@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
}
