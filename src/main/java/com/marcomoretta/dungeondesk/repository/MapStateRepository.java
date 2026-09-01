package com.marcomoretta.dungeondesk.repository;

import com.marcomoretta.dungeondesk.domain.entity.MapState;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Map State persistency layer interface
 * EntityGraphs are used to extract lazy fetch fields
 */
@Repository
public interface MapStateRepository extends JpaRepository<MapState, Long> {

    @EntityGraph(attributePaths = {"tokenList", "tokenList.sheet"})
    Optional<MapState> findByCampaign_CampaignId(Long campaignId);
}
