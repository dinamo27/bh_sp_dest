package com.example.service.impl;

import com.example.repository.ProjectRepository;
import com.example.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ProjectServiceImpl implements ProjectService {
    
    private static final String STATUS_FAILED = "FAILED";
    
    private final ProjectRepository projectRepository;
    
    @Autowired
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
                log.warn("No project found with ID {}, update failed", projectId);
            }
            
            return success;
        } catch (Exception e) {
            log.error("Error marking project with ID {} as failed: {}", projectId, e.getMessage(), e);
            throw e;
        }
    }
}