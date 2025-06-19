package com.example.controller;

import com.example.service.ProjectResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
public class ProjectResetController {
    
    private final ProjectResetService projectResetService;
    
    @Autowired
    public ProjectResetController(ProjectResetService projectResetService) {
        this.projectResetService = projectResetService;
    }
    
    @PostMapping("/{projectId}/reset")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> resetProject(@PathVariable String projectId) {
        if (projectId == null || projectId.isEmpty()) {
            return ResponseEntity.badRequest().body("Project ID cannot be empty");
        }
        
        int result = projectResetService.resetProject(projectId);
        
        if (result == 0) {
            return ResponseEntity.ok("Project reset successfully");
        } else {
            return ResponseEntity.internalServerError().body("Failed to reset project");
        }
    }
}