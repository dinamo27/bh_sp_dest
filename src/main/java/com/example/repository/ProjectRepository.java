package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.entity.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {
    
    @Transactional
    @Query(value = "CALL reset_and_finalize_project(:projectId)", nativeQuery = true)
    Integer resetAndFinalizeProject(@Param("projectId") Integer projectId);
}