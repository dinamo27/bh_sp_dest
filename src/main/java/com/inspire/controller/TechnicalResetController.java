package com.inspire.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inspire.service.TechnicalResetService;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/technical-reset")
public class TechnicalResetController {

    private final TechnicalResetService technicalResetService;

    public TechnicalResetController(TechnicalResetService technicalResetService) {
        this.technicalResetService = technicalResetService;
    }

    @PostMapping("/project/{projectId}")
    public ResponseEntity<Map<String, Object>> resetProject(@PathVariable Integer projectId) {
        if (projectId == null || projectId <= 0) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Invalid project ID"));
        }

        int result = technicalResetService.resetProject(projectId);

        if (result == 0) {
            return ResponseEntity.ok(Collections.singletonMap("message", "Project reset successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("error", "Failed to reset project"));
        }
    }
}