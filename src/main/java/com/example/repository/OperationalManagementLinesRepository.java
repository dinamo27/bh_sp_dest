package com.example.repository;

import com.example.entity.OperationalManagementLines;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OperationalManagementLinesRepository extends JpaRepository<OperationalManagementLines, Integer> {
    
    @Modifying
    @Query("UPDATE OperationalManagementLines o SET o.status = 'E', o.message = CONCAT(o.message, ' Forced om callback') WHERE o.projectId = :projectId AND o.status != 'E'")
    int forceErrorStatusForProject(@Param("projectId") Integer projectId);
}