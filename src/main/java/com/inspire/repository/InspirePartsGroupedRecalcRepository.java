package com.inspire.repository;

import com.inspire.model.InspirePartsGroupedRecalc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InspirePartsGroupedRecalcRepository extends JpaRepository<InspirePartsGroupedRecalc, Integer> {
    
    List<InspirePartsGroupedRecalc> findByProjectId(Integer projectId);
    
    @Modifying
    @Query("UPDATE InspirePartsGroupedRecalc p SET p.processedFlag = 'Y' WHERE p.projectId = :projectId")
    int markAsProcessed(@Param("projectId") Integer projectId);
    
    @Modifying
    @Query("UPDATE InspirePartsGroupedRecalc p SET p.processedFlag = 'Y' WHERE p.projectId = :projectId")
    int markAllAsProcessed(@Param("projectId") Integer projectId);
}