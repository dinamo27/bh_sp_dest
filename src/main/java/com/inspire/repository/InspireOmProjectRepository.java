package com.inspire.repository;

import com.inspire.entity.InspireOmProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InspireOmProjectRepository extends JpaRepository<InspireOmProject, Long> {
    // Define method to find records by project ID
    List<InspireOmProject> findByProjectProjectId(Long projectId);

    // Define method to update error flag for pending records by project ID
    @Modifying
    @Query("UPDATE InspireOmProject o SET o.errorFlag = :errorFlag WHERE o.project.projectId = :projectId AND (o.processedFlag = :pendingFlag OR o.processedFlag IS NULL)")
    int updateErrorFlagForPendingByProjectId(@Param("projectId") Long projectId, @Param("errorFlag") String errorFlag, @Param("pendingFlag") String pendingFlag);
}