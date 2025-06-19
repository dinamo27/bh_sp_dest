package com.inspire.repository;

import com.inspire.model.InspirePartsGroupedRecalc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InspirePartsGroupedRecalcRepository extends JpaRepository<InspirePartsGroupedRecalc, String> {
    
    @Modifying
    @Query("UPDATE InspirePartsGroupedRecalc r SET r.processedFlag = 'Y' WHERE r.projectId = :projectId AND r.processedFlag != 'Y'")
    int markAsProcessed(@Param("projectId") String projectId);
    
    @Modifying
    @Query("UPDATE InspirePartsGroupedRecalc p SET p.processedFlag = :flag WHERE p.projectId = :projectId AND p.processedFlag != :flag")
    int updateProcessedFlagByProjectId(@Param("projectId") String projectId, @Param("flag") String flag);
}