package com.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.service.ProjectService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
@Slf4j
public class ProjectController {
    
    private final ProjectService projectService;
    
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }
    
    @PatchMapping("/{projectId}/mark-failed")
    public ResponseEntity<Map<String, Object>> markProjectAsFailed(@PathVariable Long projectId) {
        log.info("Received request to mark project with ID {} as failed", projectId);
        
        boolean success = projectService.markProjectAsFailed(projectId);
        
        Map<String, Object> response = new HashMap<>();
        
        if (success) {
            response.put("success", true);
            response.put("message", "Project marked as failed successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Failed to mark project as failed. Project not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}