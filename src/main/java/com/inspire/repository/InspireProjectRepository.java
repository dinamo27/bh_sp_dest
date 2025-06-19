package com.inspire.repository;

import com.inspire.model.InspireProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface InspireProjectRepository extends JpaRepository<InspireProject, String> {
    
    @Modifying
    @Query("UPDATE InspireProject p SET p.status = :status, p.lastUpdateDate = :lastUpdateDate, p.lastUpdatedBy = :lastUpdatedBy WHERE p.projectId = :projectId AND p.status != :status")
    int updateProjectStatusByProjectId(
        @Param("projectId") String projectId,
        @Param("status") String status,
        @Param("lastUpdateDate") LocalDateTime lastUpdateDate,
        @Param("lastUpdatedBy") String lastUpdatedBy);
    
    @Modifying
    @Query("UPDATE InspireProject p SET p.status = 'COMPLETED', p.lastUpdateDate = :lastUpdateDate, p.lastUpdatedBy = :lastUpdatedBy WHERE p.projectId = :projectId AND p.status != 'COMPLETED'")
    int completeProject(
        @Param("projectId") String projectId, 
        @Param("lastUpdateDate") LocalDateTime lastUpdateDate, 
        @Param("lastUpdatedBy") String lastUpdatedBy);
}