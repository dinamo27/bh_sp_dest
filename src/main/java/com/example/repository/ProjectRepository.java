package com.example.repository;

import com.example.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    
    Project findByProjectId(Long projectId);
    
    boolean existsById(Long projectId);
    
    @Modifying
    @Transactional
    @Query("UPDATE Project p SET p.status = :status, p.recalcRequired = :recalcRequired, p.refreshRequired = :refreshRequired WHERE p.projectId = :projectId")
    int updateProjectStatusAndFlags(
        @Param("projectId") Long projectId,
        @Param("status") String status,
        @Param("recalcRequired") boolean recalcRequired,
        @Param("refreshRequired") boolean refreshRequired);
    
    @Modifying
    @Transactional
    @Query(value = "UPDATE inspire_project SET status = 'COMPLETED', recalc_required = false, refresh_required = false WHERE project_id = :projectId", nativeQuery = true)
    int updateProjectStatusForTechnicalReset(@Param("projectId") Long projectId);
    
    @Modifying
    @Transactional
    @Query(value = "UPDATE inspire_parts_grouped_recalc SET processed_flag = true WHERE project_id = :projectId AND processed_flag = false", nativeQuery = true)
    int markPartsGroupedRecalcAsProcessed(@Param("projectId") Long projectId);
    
    @Modifying
    @Transactional
    @Query(value = "UPDATE inspire_spir_refresh_data SET processed_flag = true WHERE project_id = :projectId AND processed_flag = false", nativeQuery = true)
    int markSpirRefreshDataAsProcessed(@Param("projectId") Long projectId);
    
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM inspire_temp_pos_to_activity WHERE project_id = :projectId", nativeQuery = true)
    int deleteTempPosToActivity(@Param("projectId") Long projectId);
    
    @Modifying
    @Transactional
    @Query(value = "UPDATE inspire_om_project SET error_flag = true, error_message = CONCAT(COALESCE(error_message, ''), :errorMessage) " +
           "WHERE project_id = :projectId AND processed_flag = false", nativeQuery = true)
    int setErrorFlagForOmProject(@Param("projectId") Long projectId, @Param("errorMessage") String errorMessage);
    
    @Modifying
    @Transactional
    @Query(value = "UPDATE inspire_om_lines SET error_flag = true, error_message = CONCAT(COALESCE(error_message, ''), :errorMessage) " +
           "WHERE project_id = :projectId AND processed_flag = false", nativeQuery = true)
    int setErrorFlagForOmLines(@Param("projectId") Long projectId, @Param("errorMessage") String errorMessage);
}