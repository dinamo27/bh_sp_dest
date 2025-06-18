package com.example.service.impl;

import com.example.service.ProjectService;
import com.example.repository.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectServiceImpl implements ProjectService {
    
    private static final String STATUS_FAILED = "FAILED";
    private static final Logger log = LoggerFactory.getLogger(ProjectServiceImpl.class);
    
    private final ProjectRepository projectRepository;
    
    public ProjectServiceImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }
    
    @Override
    @Transactional
    public boolean markProjectAsFailed(Long projectId) {
        try {
            log.info("Marking project with ID {} as failed", projectId);
            
            int updatedRows = projectRepository.updateProjectStatus(projectId, STATUS_FAILED);
            
            boolean success = updatedRows > 0;
            
            if (success) {
                log.info("Successfully marked project with ID {} as failed", projectId);
            } else {
                log.warn("Failed to mark project with ID {} as failed. Project not found.", projectId);
            }
            
            return success;
        } catch (Exception e) {
            log.error("Error marking project with ID {} as failed: {}", projectId, e.getMessage(), e);
            return false;
        }
    }
}