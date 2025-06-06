package com.example.project.repository;

import com.example.project.entity.InspireOmLines;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface InspireOmLinesRepository extends JpaRepository<InspireOmLines, Long> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE inspire_om_lines SET processed = 'E' WHERE spir_project_id = :spirProjectId AND (processed = 'P' OR processed IS NULL)", nativeQuery = true)
    void updateOmLines(@Param("spirProjectId") Long spirProjectId);
}