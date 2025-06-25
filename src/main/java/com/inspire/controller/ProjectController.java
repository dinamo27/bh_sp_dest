package com.inspire.controller;

import com.inspire.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService projectService;
    
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }
    
    @PostMapping("/{projectId}/technical-reset")
    public ResponseEntity<String> technicalResetProject(@PathVariable Long projectId) {
        try {
            boolean success = projectService.technicalResetProject(projectId);
            if (success) {
                return ResponseEntity.ok("Project successfully reset");
            } else {
                return ResponseEntity.internalServerError().body("Failed to reset project");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}