package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.project.entity.InspireOmProject;

@Repository
public interface ProjectRepository extends JpaRepository<InspireOmProject, Long> {
    
    @Query(value = "CALL update.inspire_om_project(:projectId)", nativeQuery = true)
    void callInspireProjectResetProcedure(@Param("projectId") Long projectId);
}