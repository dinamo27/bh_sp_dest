package com.inspire.repository;

import com.inspire.model.SpirRefreshData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpirRefreshDataRepository extends JpaRepository<SpirRefreshData, Long> {
    
    List<SpirRefreshData> findByProjectId(Integer projectId);
    
    List<SpirRefreshData> findByProjectIdAndProcessedFlag(Integer projectId, Boolean processedFlag);
    
    @Modifying
    @Query("UPDATE SpirRefreshData s SET s.processedFlag = true WHERE s.projectId = :projectId AND s.processedFlag = false")
    int markProcessedByProjectId(@Param("projectId") Integer projectId);
}