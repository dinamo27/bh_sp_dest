package com.inspire.repository;

import com.inspire.model.SpirRefreshData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SpirRefreshDataRepository extends JpaRepository<SpirRefreshData, Integer> {
    
    @Modifying
    @Query("UPDATE SpirRefreshData s SET s.processed = true WHERE s.projectId = :projectId")
    int markAsProcessedByProjectId(@Param("projectId") Integer projectId);
}