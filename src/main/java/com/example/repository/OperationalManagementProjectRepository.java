package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.entity.OperationalManagementProject;

public interface OperationalManagementProjectRepository extends JpaRepository<OperationalManagementProject, Integer> {
    
    @Modifying
    @Query("UPDATE OperationalManagementProject o SET o.status = 'E', o.message = CONCAT(o.message, ' Forced om callback') WHERE o.projectId = :projectId AND o.status != 'E'")
    int forceErrorStatusForProject(@Param("projectId") Integer projectId);
}