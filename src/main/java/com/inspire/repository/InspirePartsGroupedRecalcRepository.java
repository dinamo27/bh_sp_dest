package com.inspire.repository;

import com.inspire.model.InspirePartsGroupedRecalc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InspirePartsGroupedRecalcRepository extends JpaRepository<InspirePartsGroupedRecalc, String> {
    
    @Modifying
    @Query("UPDATE InspirePartsGroupedRecalc i SET i.processedFlag = 'Y' WHERE i.projectId = :projectId AND i.processedFlag != 'Y'")
    int updateProcessedFlagByProjectId(@Param("projectId") String projectId);
    
    List<InspirePartsGroupedRecalc> findByProjectIdAndProcessedFlagNot(String projectId, char processedFlag);
}