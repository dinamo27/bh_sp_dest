package com.inspire.repository;

import com.inspire.model.InspireOmLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InspireOmLineRepository extends JpaRepository<InspireOmLine, Integer> {
    // Find OM lines by project ID
    List<InspireOmLine> findByProjectId(Integer projectId);

    // Update processed status to 'E' for pending line items
    @Modifying
    @Query("UPDATE InspireOmLine l SET l.processed = 'E' " +
           "WHERE l.projectId = :projectId AND (l.processed = 'P' OR l.processed IS NULL)")
    int updateOmLinesProcessedStatus(@Param("projectId") Integer projectId);
    
    // Update processed status with configurable flags
    @Modifying
    @Query("UPDATE InspireOmLine l SET l.processed = :processedFlag " +
           "WHERE l.projectId = :projectId AND (l.processed = :pendingFlag OR l.processed IS NULL)")
    int updatePendingOmLines(
        @Param("projectId") Integer projectId, 
        @Param("processedFlag") Character processedFlag,
        @Param("pendingFlag") Character pendingFlag);
}