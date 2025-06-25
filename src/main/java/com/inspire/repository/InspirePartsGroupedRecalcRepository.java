package com.inspire.repository;

import com.inspire.entity.InspirePartsGroupedRecalc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InspirePartsGroupedRecalcRepository extends JpaRepository<InspirePartsGroupedRecalc, Long> {
    
    List<InspirePartsGroupedRecalc> findByProjectProjectId(Long projectId);

    @Modifying
    @Query("UPDATE InspirePartsGroupedRecalc p SET p.processedFlag = :processedFlag WHERE p.project.projectId = :projectId")
    int updateProcessedFlagByProjectId(@Param("projectId") Long projectId, @Param("processedFlag") String processedFlag);
}