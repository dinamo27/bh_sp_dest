package com.inspire.repository;

import com.inspire.entity.InspireOmLines;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InspireOmLinesRepository extends JpaRepository<InspireOmLines, Long> {
    
    List<InspireOmLines> findByProjectProjectId(Long projectId);
    
    @Modifying
    @Query("UPDATE InspireOmLines o SET o.errorFlag = :errorFlag WHERE o.project.projectId = :projectId AND (o.processedFlag = :pendingFlag OR o.processedFlag IS NULL)")
    int updateErrorFlagForPendingByProjectId(@Param("projectId") Long projectId, @Param("errorFlag") String errorFlag, @Param("pendingFlag") String pendingFlag);
}