package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.entity.PartsGroupedRecalc;

@Repository
public interface PartsGroupedRecalcRepository extends JpaRepository<PartsGroupedRecalc, Long> {
    
    @Modifying
    @Transactional
    @Query(value = "UPDATE inspire_parts_grouped_recalc SET processed_flag = true WHERE project_id = :projectId AND processed_flag = false", nativeQuery = true)
    int markAllAsProcessedByProjectId(@Param("projectId") Long projectId);
}