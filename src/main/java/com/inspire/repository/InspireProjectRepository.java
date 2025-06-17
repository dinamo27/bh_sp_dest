package com.inspire.repository;

import com.inspire.model.InspireProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface InspireProjectRepository extends JpaRepository<InspireProject, Integer> {
    // Find a project by its ID
    Optional<InspireProject> findByProjectId(Integer projectId);
    
    // Update project status to COMPLETED
    @Modifying
    @Query("UPDATE InspireProject p SET p.status = 'COMPLETED' WHERE p.projectId = :projectId")
    int updateProjectStatusToCompleted(@Param("projectId") Integer projectId);
    
    // Update project status
    @Modifying
    @Query("UPDATE InspireProject p SET p.status = :status WHERE p.projectId = :projectId")
    int updateProjectStatus(@Param("projectId") Integer projectId, @Param("status") String status);
}