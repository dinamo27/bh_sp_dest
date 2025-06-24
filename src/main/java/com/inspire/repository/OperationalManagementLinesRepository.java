package com.inspire.repository;

import com.inspire.model.OperationalManagementLines;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationalManagementLinesRepository extends JpaRepository<OperationalManagementLines, Integer> {
    
    @Modifying
    @Query("UPDATE OperationalManagementLines o SET o.processingStatus = 'E', o.message = :message " +
           "WHERE o.projectId = :projectId AND (o.processingStatus = 'P' OR o.processingStatus IS NULL)")
    int updatePendingToErrorByProjectId(@Param("projectId") Integer projectId, @Param("message") String message);
}