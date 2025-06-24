package com.inspire.repository;

import com.inspire.model.OperationalManagementProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationalManagementProjectRepository extends JpaRepository<OperationalManagementProject, Integer> {
    
    @Modifying
    @Query("UPDATE OperationalManagementProject o SET o.processingStatus = 'E', o.message = :message " +
           "WHERE o.projectId = :projectId AND (o.processingStatus = 'P' OR o.processingStatus IS NULL)")
    int updatePendingToErrorByProjectId(@Param("projectId") Integer projectId, @Param("message") String message);
}