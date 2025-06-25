package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.model.InspireProject;

@Repository
public interface InspireProjectRepository extends JpaRepository<InspireProject, Long> {
    
    InspireProject findByProjectId(Long projectId);
    
    @Modifying
    @Transactional
    @Query("UPDATE InspireProject p SET p.status = :status WHERE p.projectId = :projectId")
    int updateProjectStatus(@Param("projectId") Long projectId, @Param("status") String status);
    
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM position_to_activity WHERE project_id = :projectId", nativeQuery = true)
    int deletePositionToActivityMappings(@Param("projectId") Long projectId);
    
    @Modifying
    @Transactional
    @Query(value = "UPDATE inspire_parts_grouped_recalc SET processed_flag = :processedFlag WHERE project_id = :projectId", nativeQuery = true)
    int updatePartsGroupedRecalcProcessed(@Param("projectId") Long projectId, @Param("processedFlag") String processedFlag);
    
    @Modifying
    @Transactional
    @Query(value = "UPDATE inspire_spir_refresh_data SET processed_flag = :processedFlag WHERE project_id = :projectId", nativeQuery = true)
    int updateSpirRefreshDataProcessed(@Param("projectId") Long projectId, @Param("processedFlag") String processedFlag);
    
    @Modifying
    @Transactional
    @Query(value = "UPDATE inspire_om_project SET error_flag = :errorFlag WHERE project_id = :projectId AND (processed_flag = :pendingFlag OR processed_flag IS NULL)", nativeQuery = true)
    int updateOmProjectErrorStatus(@Param("projectId") Long projectId, @Param("errorFlag") String errorFlag, @Param("pendingFlag") String pendingFlag);
    
    @Modifying
    @Transactional
    @Query(value = "UPDATE inspire_om_lines SET error_flag = :errorFlag WHERE project_id = :projectId AND (processed_flag = :pendingFlag OR processed_flag IS NULL)", nativeQuery = true)
    int updateOmLinesErrorStatus(@Param("projectId") Long projectId, @Param("errorFlag") String errorFlag, @Param("pendingFlag") String pendingFlag);
}