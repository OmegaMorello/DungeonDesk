package com.marcomoretta.dungeondesk.repository;

import com.marcomoretta.dungeondesk.domain.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Note persistency layer interface
 */
@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
}
