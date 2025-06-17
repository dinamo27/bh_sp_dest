package com.inspire.repository;

import com.inspire.entity.InspireProject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InspireProjectRepository extends JpaRepository<InspireProject, String> {
    
    boolean existsByProjectId(String projectId);
    
    InspireProject findByProjectId(String projectId);
}