package com.inspire.controller;

import com.inspire.service.ProjectTechnicalResetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
public class ProjectTechnicalResetController {
    private static final Logger logger = LoggerFactory.getLogger(ProjectTechnicalResetController.class);
    
    @Autowired
    private ProjectTechnicalResetService projectTechnicalResetService;
    
    /**
     * Endpoint to perform a technical reset on a stalled project.
     * 
     * @param projectId The ID of the project to reset
     * @return ResponseEntity with success/failure message and appropriate HTTP status
     */
    @PostMapping("/{projectId}/technical-reset")
    public ResponseEntity<Map<String, Object>> technicalResetProject(@PathVariable Integer projectId) {
        logger.info("Received request to perform technical reset for project ID: {}", projectId);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            int result = projectTechnicalResetService.technicalResetProject(projectId);
            
            if (result == 0) {
                // Success
                response.put("success", true);
                response.put("message", "Project technical reset completed successfully");
                response.put("projectId", projectId);
                logger.info("Technical reset completed successfully for project ID: {}", projectId);
                return ResponseEntity.ok(response);
            } else {
                // Service-level failure
                response.put("success", false);
                response.put("message", "Project technical reset failed");
                response.put("projectId", projectId);
                logger.warn("Technical reset for project ID: {} failed with result code: {}", projectId, result);
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            // Unexpected error
            response.put("success", false);
            response.put("message", "An unexpected error occurred during project technical reset");
            response.put("error", e.getMessage());
            response.put("projectId", projectId);
            logger.error("Error during technical reset for project ID: {}", projectId, e);
            return ResponseEntity.internalServerError().body(response);
        }
    }
}