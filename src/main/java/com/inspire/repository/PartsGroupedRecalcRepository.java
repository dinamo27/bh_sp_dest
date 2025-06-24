package com.inspire.repository;

import com.inspire.model.PartsGroupedRecalc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PartsGroupedRecalcRepository extends JpaRepository<PartsGroupedRecalc, Integer> {
    
    @Modifying
    @Query("UPDATE PartsGroupedRecalc p SET p.processed = true WHERE p.projectId = :projectId")
    int markAsProcessedByProjectId(@Param("projectId") Integer projectId);
}