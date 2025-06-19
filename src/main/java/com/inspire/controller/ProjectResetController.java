package com.inspire.controller;

import com.inspire.service.ProjectResetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class ProjectResetController {
    private final ProjectResetService projectResetService;

    @PostMapping("/reset")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> resetProject(@RequestParam("projectId") String projectId) {
        log.info("Received request to reset project: {}", projectId);
        
        try {
            int result = projectResetService.resetProject(projectId);
            
            if (result == 0) {
                return ResponseEntity.ok("Project reset completed successfully");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Project reset failed");
            }
        } catch (Exception e) {
            log.error("Error during project reset: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error during project reset: " + e.getMessage());
        }
    }
}