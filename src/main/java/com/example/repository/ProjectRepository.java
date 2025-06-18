package com.example.repository;

import com.example.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Integer> {
    
    Optional<Project> findById(Integer projectId);
    
    @Modifying
    @Query("UPDATE Project p SET p.status = :status WHERE p.projectId = :projectId")
    int updateProjectStatus(@Param("projectId") Integer projectId, @Param("status") String status);
}