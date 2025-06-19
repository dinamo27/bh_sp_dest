package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.service.TechnicalResetService;

@RestController
@RequestMapping("/api/technical-reset")
public class TechnicalResetController {

    private final TechnicalResetService technicalResetService;

    @Autowired
    public TechnicalResetController(TechnicalResetService technicalResetService) {
        this.technicalResetService = technicalResetService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> resetProject(@RequestParam("project_id") String projectId) {
        try {
            boolean result = technicalResetService.resetProject(projectId);
            if (result) {
                return ResponseEntity.ok("Project reset successfully");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to reset project");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during project reset: " + e.getMessage());
        }
    }
}