package com.inspire.controller;

import com.inspire.service.ProjectResetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
public class ProjectResetController {
    private static final Logger logger = LoggerFactory.getLogger(ProjectResetController.class);

    private final ProjectResetService projectResetService;

    @Autowired
    public ProjectResetController(ProjectResetService projectResetService) {
        this.projectResetService = projectResetService;
    }

    /**
     * Resets and finalizes a project by its ID.
     * This endpoint updates the project status to COMPLETED and forces callbacks on related Order Management records.
     *
     * @param projectId The ID of the project to reset and finalize
     * @return ResponseEntity with appropriate status code
     */
    @PostMapping("/{projectId}/reset-and-finalize")
    public ResponseEntity<?> resetAndFinalizeProject(@PathVariable Integer projectId) {
        logger.info("Received request to reset and finalize project ID: {}", projectId);

        try {
            boolean success = projectResetService.resetAndFinalizeProject(projectId);

            if (success) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Project reset and finalization completed successfully");
                response.put("projectId", projectId);
                
                logger.info("Successfully reset and finalized project ID: {}", projectId);
                return ResponseEntity.ok(response);
            } else {
                logger.warn("Failed to reset and finalize project ID: {}", projectId);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "success", false,
                        "message", "Failed to reset and finalize project",
                        "projectId", projectId
                    ));
            }
        } catch (Exception e) {
            logger.error("Error occurred while resetting and finalizing project ID: {}", projectId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "message", "Error occurred: " + e.getMessage(),
                    "projectId", projectId
                ));
        }
    }
}