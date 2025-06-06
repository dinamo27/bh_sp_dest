package com.example.project.repository;

import com.example.project.entity.InspireOmLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.QueryHint;
import java.util.List;

@Repository
public interface InspireOmLineRepository extends JpaRepository<InspireOmLine, Long> {

    @Transactional
    @Modifying
    @Query(value = "UPDATE inspire_om_lines SET processed = 'E' WHERE spir_project_id = :spirProjectId AND (processed IS NULL OR processed = 'P')", nativeQuery = true)
    void updateProcessed(@Param("spirProjectId") Long spirProjectId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE inspire_om_project SET process_flag = 'E', error_message = 'Forced om callback' WHERE spir_project_id = :spirProjectId AND (process_flag = 'P' OR process_flag IS NULL)", nativeQuery = true)
    void updateProjectStatus(@Param("spirProjectId") Long spirProjectId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM inspire_om_lines WHERE spir_project_id = :spirProjectId", nativeQuery = true)
    void deleteBySpirProjectId(@Param("spirProjectId") Long spirProjectId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE inspire_om_lines SET processed = :processed WHERE spir_project_id = :spirProjectId", nativeQuery = true)
    void updateOmLine(@Param("spirProjectId") Long spirProjectId, @Param("processed") String processed);
}