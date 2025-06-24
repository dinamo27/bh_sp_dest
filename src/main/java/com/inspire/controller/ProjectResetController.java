package com.inspire.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.inspire.service.ProjectResetService;

@RestController
@RequestMapping("/api/projects")
public class ProjectResetController {
    private static final Logger logger = LoggerFactory.getLogger(ProjectResetController.class);

    private final ProjectResetService projectResetService;

    @Autowired
    public ProjectResetController(ProjectResetService projectResetService) {
        this.projectResetService = projectResetService;
    }

    @PostMapping("/{projectId}/reset")
    public ResponseEntity<String> resetProject(@PathVariable Integer projectId) {
        logger.info("Received request to reset project with ID: {}", projectId);

        try {
            int result = projectResetService.resetProject(projectId);

            if (result == 0) {
                logger.info("Successfully reset project with ID: {}", projectId);
                return ResponseEntity.ok("Project reset completed successfully");
            } else {
                logger.error("Failed to reset project with ID: {}", projectId);
                return ResponseEntity.internalServerError().body("Project reset failed");
            }
        } catch (Exception e) {
            logger.error("Error resetting project with ID: {}: {}", projectId, e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error processing reset request: " + e.getMessage());
        }
    }
}