package com.inspire.repository;

import com.inspire.model.InspireOmLines;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InspireOmLinesRepository extends JpaRepository<InspireOmLines, String> {
    
    @Modifying
    @Query("UPDATE InspireOmLines l SET l.status = 'E', l.errorMessage = :errorMessage WHERE l.projectId = :projectId AND l.status = 'P'")
    int markPendingWithError(
            @Param("projectId") String projectId, 
            @Param("errorMessage") String errorMessage);
            
    @Modifying
    @Query("UPDATE InspireOmLines l SET l.status = :status, l.errorMessage = :errorMessage WHERE l.projectId = :projectId AND l.status = :pendingStatus")
    int updateStatusAndErrorMessageForPendingByProjectId(
        @Param("projectId") String projectId,
        @Param("status") String status,
        @Param("errorMessage") String errorMessage,
        @Param("pendingStatus") String pendingStatus);
}