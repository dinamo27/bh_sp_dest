package com.inspire.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.inspire.model.InspirePartsGroupedRecalc;

@Repository
public interface InspirePartsGroupedRecalcRepository extends JpaRepository<InspirePartsGroupedRecalc, Long> {
    
    @Modifying
    @Transactional
    @Query("UPDATE InspirePartsGroupedRecalc p SET p.processedFlag = :processedFlag WHERE p.projectId = :projectId")
    int updateProcessedFlagByProjectId(Integer projectId, Character processedFlag);
}