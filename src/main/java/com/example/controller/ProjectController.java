package com.example.controller;

import com.example.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
@Slf4j
public class ProjectController {
    
    private final ProjectService projectService;
    
    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }
    
    @PutMapping("/{projectId}/mark-failed")
    public ResponseEntity<Map<String, Object>> markProjectAsFailed(@PathVariable Long projectId) {
        log.info("Received request to mark project with ID {} as failed", projectId);
        
        try {
            boolean success = projectService.markProjectAsFailed(projectId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            
            if (success) {
                response.put("message", "Project marked as failed successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Project not found or could not be updated");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            log.error("Error processing request to mark project as failed: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to mark project as failed due to an internal error");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}