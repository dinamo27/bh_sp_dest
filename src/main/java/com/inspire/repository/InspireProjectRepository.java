package com.inspire.repository;

import com.inspire.model.InspireProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InspireProjectRepository extends JpaRepository<InspireProject, Integer> {
    
    InspireProject findByProjectId(Integer projectId);
    
    @Modifying
    @Query("UPDATE InspireProject p SET p.status = 'COMPLETED' WHERE p.projectId = :projectId")
    int updateStatusToCompleted(@Param("projectId") Integer projectId);
    
    @Modifying
    @Query("UPDATE InspireProject p SET p.status = :status WHERE p.projectId = :projectId")
    int updateStatus(@Param("projectId") Integer projectId, @Param("status") String status);
}