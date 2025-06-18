package com.example.repository;

import com.example.entity.PositionToActivityTemp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PositionToActivityTempRepository extends JpaRepository<PositionToActivityTemp, Integer> {
    
    @Modifying
    @Query("DELETE FROM PositionToActivityTemp p WHERE p.projectId = :projectId")
    int deleteAllByProjectId(@Param("projectId") Integer projectId);
}