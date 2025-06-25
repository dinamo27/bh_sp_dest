package com.inspire.repository;

import com.inspire.entity.InspireSpirRefreshData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface InspireSpirRefreshDataRepository extends JpaRepository<InspireSpirRefreshData, Long> {
    
    List<InspireSpirRefreshData> findByProjectProjectId(Long projectId);
    
    @Modifying
    @Transactional
    @Query("UPDATE InspireSpirRefreshData s SET s.processedFlag = :processedFlag WHERE s.project.projectId = :projectId")
    int updateProcessedFlagByProjectId(@Param("projectId") Long projectId, @Param("processedFlag") String processedFlag);
}