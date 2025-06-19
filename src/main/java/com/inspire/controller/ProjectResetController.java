package com.inspire.controller;

import com.inspire.service.ProjectResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
public class ProjectResetController {
    
    private final ProjectResetService projectResetService;
    
    @Autowired
    public ProjectResetController(ProjectResetService projectResetService) {
        this.projectResetService = projectResetService;
    }
    
    @PostMapping("/reset")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> resetProject(@RequestParam String projectId) {
        if (projectId == null || projectId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Project ID is required");
        }
        
        int result = projectResetService.resetProject(projectId);
        
        if (result == 0) {
            return ResponseEntity.ok("Project reset completed successfully");
        } else {
            return ResponseEntity.internalServerError().body("Project reset failed");
        }
    }
}