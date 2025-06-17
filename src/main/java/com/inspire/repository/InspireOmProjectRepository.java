package com.inspire.repository;

import com.inspire.model.InspireOmProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InspireOmProjectRepository extends JpaRepository<InspireOmProject, Integer> {
    // Find OM projects by project ID
    List<InspireOmProject> findByProjectId(Integer projectId);

    // Update process flag to 'E' and set error message for pending records
    @Modifying
    @Query("UPDATE InspireOmProject o SET o.processFlag = 'E', o.errorMessage = 'Forced om callback' " +
           "WHERE o.projectId = :projectId AND (o.processFlag = 'P' OR o.processFlag IS NULL)")
    int updateOmProjectsForCallback(@Param("projectId") Integer projectId);
    
    // More flexible update method for process flag and error message for pending records
    @Modifying
    @Query("UPDATE InspireOmProject o SET o.processFlag = :processFlag, o.errorMessage = :errorMessage " +
           "WHERE o.projectId = :projectId AND (o.processFlag = :pendingFlag OR o.processFlag IS NULL)")
    int updatePendingOmProjects(
        @Param("projectId") Integer projectId, 
        @Param("processFlag") Character processFlag, 
        @Param("errorMessage") String errorMessage,
        @Param("pendingFlag") Character pendingFlag);
}