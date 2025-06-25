package com.inspire.controller;

import com.inspire.service.SpirProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/spir-projects")
public class SpirProjectController {

    @Autowired
    private SpirProjectService spirProjectService;

    @PostMapping("/{projectId}/reset-finalize")
    public ResponseEntity<Map<String, Object>> resetAndFinalizeProject(@PathVariable Long projectId) {
        int result = spirProjectService.resetAndFinalizeProject(projectId);
        
        Map<String, Object> response = new HashMap<>();
        
        if (result == 0) {
            response.put("message", "Project reset and finalized successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Failed to reset and finalize project");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}