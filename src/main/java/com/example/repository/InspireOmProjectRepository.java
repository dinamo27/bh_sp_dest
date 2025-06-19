package com.example.repository;

import com.example.model.InspireOmProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InspireOmProjectRepository extends JpaRepository<InspireOmProject, String> {
    
    @Modifying
    @Query("UPDATE InspireOmProject p SET p.status = 'E', p.errorMessage = :errorMessage " +
           "WHERE p.projectId = :projectId AND p.status = 'P'")
    int markPendingWithError(
        @Param("projectId") String projectId, 
        @Param("errorMessage") String errorMessage
    );
}