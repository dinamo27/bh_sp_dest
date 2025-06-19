package com.example.repository;

import com.example.model.InspireProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface InspireProjectRepository extends JpaRepository<InspireProject, String> {
    @Modifying
    @Query("UPDATE InspireProject p SET p.status = 'COMPLETED', p.lastUpdateDate = :updateDate, p.lastUpdatedBy = :updatedBy " +
           "WHERE p.projectId = :projectId AND p.status != 'COMPLETED'")
    int completeProject(
        @Param("projectId") String projectId, 
        @Param("updateDate") LocalDateTime updateDate, 
        @Param("updatedBy") String updatedBy
    );
}