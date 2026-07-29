package com.marcomoretta.dungeondesk.repository;

import com.marcomoretta.dungeondesk.domain.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Campaign persistency layer interface
 */
@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {
}
