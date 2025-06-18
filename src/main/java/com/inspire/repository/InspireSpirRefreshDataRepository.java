package com.inspire.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.inspire.model.InspireSpirRefreshData;

@Repository
public interface InspireSpirRefreshDataRepository extends JpaRepository<InspireSpirRefreshData, Long> {
    
    @Modifying
    @Transactional
    @Query("UPDATE InspireSpirRefreshData s SET s.processedFlag = :processedFlag WHERE s.projectId = :projectId")
    int updateProcessedFlagByProjectId(Integer projectId, Character processedFlag);
}