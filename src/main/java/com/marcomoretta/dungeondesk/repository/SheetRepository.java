package com.marcomoretta.dungeondesk.repository;

import com.marcomoretta.dungeondesk.domain.entity.GenericSheet;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Sheet persistency layer interface, polymorphic over both kinds of sheet
 * EntityGraphs are used to extract lazy fetch fields
 * All the collections are Sets because two Lists in the same graph would raise MultipleBagFetchException
 */
@Repository
public interface SheetRepository extends JpaRepository<GenericSheet, Long> {

    @Override
    @EntityGraph(attributePaths = {
            "attacks", "spellSlots",
            "skillProficiencies", "skillExpertise", "savingThrowProficiencies"})
    Optional<GenericSheet> findById(Long sheetId);

    /**
     * Every sheet in a Dungeon Master library
     *
     * @param ownerId The owner to read
     * @return The sheets, ordered by name
     */
    @EntityGraph(attributePaths = {
            "attacks", "spellSlots",
            "skillProficiencies", "skillExpertise", "savingThrowProficiencies"})
    List<GenericSheet> findByOwner_UserIdOrderByNameAsc(Long ownerId);
}
