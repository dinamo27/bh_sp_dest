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
    @Query("UPDATE InspireProject i SET i.status = :status, i.lastUpdateDate = :lastUpdateDate, i.lastUpdatedBy = :lastUpdatedBy WHERE i.projectId = :projectId AND i.status != :status")
    int updateProjectStatus(
        @Param("projectId") String projectId,
        @Param("status") String status,
        @Param("lastUpdateDate") LocalDateTime lastUpdateDate,
        @Param("lastUpdatedBy") String lastUpdatedBy
    );
    
    // All other required methods are inherited from JpaRepository, including findById
}