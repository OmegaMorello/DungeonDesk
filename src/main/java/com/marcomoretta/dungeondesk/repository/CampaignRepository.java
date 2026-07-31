package com.marcomoretta.dungeondesk.repository;

import com.marcomoretta.dungeondesk.domain.entity.Campaign;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Campaign persistency layer interface
 */
@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    @Override
    @EntityGraph(attributePaths = {"players", "owner"})
    List<Campaign> findAll();

    @Override
    @EntityGraph(attributePaths = {"players", "owner"})
    Optional<Campaign> findById(Long id);
}
