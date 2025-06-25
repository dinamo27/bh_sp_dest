package com.inspire.repository;

import com.inspire.entity.PositionToActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PositionToActivityRepository extends JpaRepository<PositionToActivity, Long> {
    // Define method to find records by project ID
    List<PositionToActivity> findByProjectProjectId(Long projectId);

    // Define method to delete records by project ID
    @Modifying
    @Query("DELETE FROM PositionToActivity p WHERE p.project.projectId = :projectId")
    int deleteByProjectId(@Param("projectId") Long projectId);
}