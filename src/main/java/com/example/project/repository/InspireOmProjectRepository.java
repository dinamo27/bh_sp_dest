package com.example.project.repository;

import com.example.project.entity.InspireOmProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InspireOmProjectRepository extends JpaRepository<InspireOmProject, Long> {
    
    @Modifying
    @Query("UPDATE InspireOmProject p SET p.processFlag = 'E', p.errorMessage = :errorMessage WHERE p.spirProjectId = :spirProjectId AND COALESCE(p.processFlag, 'P') = 'P'")
    int updatePendingProjectsToError(@Param("spirProjectId") Long spirProjectId, @Param("errorMessage") String errorMessage);
}