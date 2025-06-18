package com.inspire.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.inspire.model.Project;
import com.inspire.repository.ProjectRepository;
import com.inspire.service.ProjectStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ProjectStatusServiceImpl implements ProjectStatusService {
    private static final Logger logger = LoggerFactory.getLogger(ProjectStatusServiceImpl.class);
    private static final String FAILED_STATUS = "FAILED";

    private final ProjectRepository projectRepository;
    
    @Autowired
    public ProjectStatusServiceImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    @Transactional
    public int markProjectAsFailed(Integer projectId) {
        int returnCode = -1;

        try {
            Project project = projectRepository.findByProjectId(projectId);
            
            if (project != null) {
                logger.info("Marking project {} as failed", projectId);
                
                int updatedRows = projectRepository.updateProjectStatus(projectId, FAILED_STATUS);
                
                if (updatedRows > 0) {
                    returnCode = 0;
                    logger.info("Successfully marked project {} as failed", projectId);
                } else {
                    logger.error("Failed to update status for project {}", projectId);
                }
            } else {
                logger.error("Project with ID {} not found", projectId);
            }
        } catch (Exception e) {
            logger.error("Error marking project {} as failed: {}", projectId, e.getMessage(), e);
        }
        
        return returnCode;
    }
}