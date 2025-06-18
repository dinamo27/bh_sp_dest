package com.example.repository;

import com.example.entity.SpirRefreshData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpirRefreshDataRepository extends JpaRepository<SpirRefreshData, Integer> {
    
    @Modifying
    @Query("UPDATE SpirRefreshData s SET s.processed = true WHERE s.projectId = :projectId")
    int markAllProcessedByProjectId(@Param("projectId") Integer projectId);
}