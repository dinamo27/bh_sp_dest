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
    
    @Modifying
    @Query("UPDATE InspireOmProject p SET p.status = 'E' WHERE p.projectId = :projectId AND p.status = 'P'")
    int updatePendingToError(@Param("projectId") Integer projectId);
    
    List<InspireOmProject> findByProjectId(Integer projectId);
}