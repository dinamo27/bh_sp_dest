package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.entity.OmProject;

@Repository
public interface OmProjectRepository extends JpaRepository<OmProject, Long> {
    
    @Modifying
    @Transactional
    @Query(value = "UPDATE inspire_om_project SET error_flag = true, error_message = CONCAT(COALESCE(error_message, ''), ' Forced om callback') WHERE project_id = :projectId AND processed_flag = false", nativeQuery = true)
    int forceErrorStateByProjectId(@Param("projectId") Long projectId);
}