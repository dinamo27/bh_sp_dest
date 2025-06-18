package com.inspire.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.inspire.model.InspireOmLines;

@Repository
public interface InspireOmLinesRepository extends JpaRepository<InspireOmLines, Long> {
    
    @Modifying
    @Transactional
    @Query("UPDATE InspireOmLines l SET l.status = :newStatus WHERE l.projectId = :projectId AND (l.status = :pendingStatus OR l.processedFlag != :processedFlag)")
    int updateStatusForPendingOrUnprocessedRecords(Integer projectId, Character pendingStatus, Character newStatus, Character processedFlag);
    
    @Query("SELECT l FROM InspireOmLines l WHERE l.projectId = :projectId")
    Iterable<InspireOmLines> findByProjectId(Integer projectId);
    
    @Query("SELECT l FROM InspireOmLines l WHERE l.status = :status")
    Iterable<InspireOmLines> findByStatus(Character status);
    
    @Query("SELECT l FROM InspireOmLines l WHERE l.processedFlag = :processedFlag")
    Iterable<InspireOmLines> findByProcessedFlag(Character processedFlag);
    
    @Modifying
    @Transactional
    @Query("UPDATE InspireOmLines l SET l.processedFlag = :processedFlag WHERE l.projectId = :projectId")
    int updateProcessedFlagByProjectId(Integer projectId, Character processedFlag);
}