package com.marcomoretta.dungeondesk.repository;

import com.marcomoretta.dungeondesk.domain.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * AppUser persistency layer interface
 */
@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
}
