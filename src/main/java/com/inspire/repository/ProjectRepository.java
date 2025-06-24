package com.inspire.repository;

import com.inspire.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {
    
    @Modifying
    @Query("UPDATE Project p SET p.status = 'COMPLETED' WHERE p.projectId = :projectId")
    int updateProjectStatusToCompleted(@Param("projectId") Integer projectId);
}