package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.entity.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    
    @Modifying
    @Query("UPDATE Project p SET p.spirStatus = :status WHERE p.projectId = :projectId")
    int updateProjectStatus(@Param("projectId") Long projectId, @Param("status") String status);
}