package com.inspire.repository;

import com.inspire.model.PositionToActivityMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionToActivityMappingRepository extends JpaRepository<PositionToActivityMapping, Integer> {
    
    @Modifying
    @Query("DELETE FROM PositionToActivityMapping p WHERE p.projectId = :projectId")
    int deleteByProjectId(@Param("projectId") Integer projectId);
}