package com.inspire.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.inspire.model.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {
    
    Project findByProjectId(Integer projectId);
    
    @Modifying
    @Transactional
    @Query("UPDATE Project p SET p.status = :status WHERE p.projectId = :projectId")
    int updateProjectStatus(Integer projectId, String status);
}