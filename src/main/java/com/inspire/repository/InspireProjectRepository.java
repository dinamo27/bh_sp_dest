package com.inspire.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inspire.entity.InspireProject;

@Repository
public interface InspireProjectRepository extends JpaRepository<InspireProject, Long> {
    
    boolean existsByProjectId(String projectId);
}