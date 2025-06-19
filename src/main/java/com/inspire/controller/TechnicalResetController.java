package com.inspire.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.inspire.service.TechnicalResetService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/technical")
@Slf4j
public class TechnicalResetController {

    private final TechnicalResetService technicalResetService;

    @Autowired
    public TechnicalResetController(TechnicalResetService technicalResetService) {
        this.technicalResetService = technicalResetService;
    }

    @PostMapping("/reset/{projectId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> resetProject(@PathVariable String projectId) {
        log.info("Received request to perform technical reset for project {}", projectId);

        int result = technicalResetService.resetProject(projectId);

        Map<String, Object> response = new HashMap<>();
        if (result == 0) {
            response.put("status", "success");
            response.put("message", "Technical reset completed successfully for project " + projectId);
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "Technical reset failed for project " + projectId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}