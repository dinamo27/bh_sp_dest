package com.inspire.repository;

import com.inspire.model.InspireOmProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InspireOmProjectRepository extends JpaRepository<InspireOmProject, String> {
    
    @Modifying
    @Query("UPDATE InspireOmProject i SET i.status = 'E', i.errorMessage = :errorMessage WHERE i.projectId = :projectId AND i.status = 'P'")
    int updatePendingProjectsWithError(
        @Param("projectId") String projectId,
        @Param("errorMessage") String errorMessage
    );
    
    List<InspireOmProject> findByProjectIdAndStatus(String projectId, char status);
}