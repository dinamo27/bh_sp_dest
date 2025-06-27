package com.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.example.project.repository.InspireOmProjectRepository;
import com.example.project.service.InspireOmLineService;
import java.util.Map;

@Service
public class InspireOmProjectService {
    
    private static final Logger logger = LoggerFactory.getLogger(InspireOmProjectService.class);
    
    private final InspireOmProjectRepository inspireOmProjectRepository;
    private final InspireOmLineService inspireOmLineService;
    
    public InspireOmProjectService(InspireOmProjectRepository inspireOmProjectRepository,
                                  InspireOmLineService inspireOmLineService) {
        this.inspireOmProjectRepository = inspireOmProjectRepository;
        this.inspireOmLineService = inspireOmLineService;
    }
    
    public void updateInspireOmProjectWithErrorStatus(Long projectId, String errorMessage) {
        if (projectId == null) {
            throw new IllegalArgumentException("Project ID cannot be null");
        }
        
        if (errorMessage == null || errorMessage.trim().isEmpty()) {
            throw new IllegalArgumentException("Error message cannot be null or empty");
        }
        
        try {
            int updatedProjects = inspireOmProjectRepository.updateProcessFlagForPendingRecordsByProjectId(projectId);
            
            logger.info("Updated {} project records to error status for project ID: {}", updatedProjects, projectId);
            
            Map<String, Object> lineUpdateResult = inspireOmLineService.updateInspireOmLinesWithErrorStatus(
                projectId, null, "", "", "", "", "");
            
            logger.info("Updated line items for project ID: {}, result: {}", projectId, lineUpdateResult);
            
        } catch (Exception e) {
            logger.error("Failed to update project with ID: {} to error status. Error: {}", projectId, e.getMessage(), e);
            
            throw new RuntimeException("Failed to update project status", e);
        }
    }
}