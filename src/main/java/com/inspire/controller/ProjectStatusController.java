package com.inspire.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.inspire.service.ProjectStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/projects")
public class ProjectStatusController {
    private static final Logger logger = LoggerFactory.getLogger(ProjectStatusController.class);

    private final ProjectStatusService projectStatusService;
    
    @Autowired
    public ProjectStatusController(ProjectStatusService projectStatusService) {
        this.projectStatusService = projectStatusService;
    }

    /**
     * Endpoint to mark a project as failed
     * @param projectId The ID of the project to mark as failed
     * @return ResponseEntity with appropriate status code
     */
    @PutMapping("/{projectId}/mark-failed")
    public ResponseEntity<String> markProjectAsFailed(@PathVariable Integer projectId) {
        logger.info("Received request to mark project {} as failed", projectId);
        
        try {
            int result = projectStatusService.markProjectAsFailed(projectId);
            
            if (result == 0) {
                logger.info("Successfully marked project {} as failed", projectId);
                return ResponseEntity.ok("Project successfully marked as failed");
            } else {
                logger.error("Failed to mark project {} as failed, service returned error code: {}", projectId, result);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to mark project as failed");
            }
        } catch (Exception e) {
            logger.error("Exception occurred while marking project {} as failed: {}", projectId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred: " + e.getMessage());
        }
    }
}