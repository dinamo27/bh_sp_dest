package com.inspire.repository;

import com.inspire.model.PartsGroupedRecalc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartsGroupedRecalcRepository extends JpaRepository<PartsGroupedRecalc, Long> {

    List<PartsGroupedRecalc> findByProjectId(Integer projectId);

    List<PartsGroupedRecalc> findByProjectIdAndProcessedFlag(Integer projectId, Boolean processedFlag);

    @Modifying
    @Query("UPDATE PartsGroupedRecalc p SET p.processedFlag = true WHERE p.projectId = :projectId AND p.processedFlag = false")
    int markProcessedByProjectId(@Param("projectId") Integer projectId);
}