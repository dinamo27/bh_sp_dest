package com.inspire.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.inspire.service.ProjectTechnicalResetService;

import java.util.Map;

@RestController
@RequestMapping("/api/projects")
public class ProjectTechnicalResetController {
    private static final Logger logger = LoggerFactory.getLogger(ProjectTechnicalResetController.class);

    private final ProjectTechnicalResetService projectTechnicalResetService;

    @Autowired
    public ProjectTechnicalResetController(ProjectTechnicalResetService projectTechnicalResetService) {
        this.projectTechnicalResetService = projectTechnicalResetService;
    }

    @PostMapping("/{projectId}/technical-reset")
    public ResponseEntity<Object> technicalReset(@PathVariable Integer projectId) {
        logger.info("Received request to perform technical reset for project ID: {}", projectId);
        
        int result = projectTechnicalResetService.performTechnicalReset(projectId);
        
        if (result == 0) {
            logger.info("Technical reset successful for project ID: {}", projectId);
            return ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "Technical reset completed successfully",
                "projectId", projectId
            ));
        } else {
            logger.warn("Technical reset failed for project ID: {}", projectId);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Technical reset failed",
                "projectId", projectId
            ));
        }
    }
}