package com.example.repository;

import com.example.entity.PartsGroupedRecalc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PartsGroupedRecalcRepository extends JpaRepository<PartsGroupedRecalc, Integer> {
    
    @Modifying
    @Query("UPDATE PartsGroupedRecalc p SET p.processed = true WHERE p.projectId = :projectId")
    int markAllProcessedByProjectId(@Param("projectId") Integer projectId);
}