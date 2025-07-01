package com.example.project.controller;

import com.example.project.service.InspireService;
import com.example.project.repository.InspireOmLinesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class InspireController {

    private final InspireService inspireService;
    private final InspireOmLinesRepository inspireOmLinesRepository;

    @Autowired
    public InspireController(InspireService inspireService, InspireOmLinesRepository inspireOmLinesRepository) {
        this.inspireService = inspireService;
        this.inspireOmLinesRepository = inspireOmLinesRepository;
    }

    @PostMapping("/delete-inspire-project")
    public ResponseEntity<Map<String, Object>> deleteInspireProject(@RequestBody Map<String, Object> request) {
        String spirProjectId = (String) request.get("spirProjectId");
        String logId = (String) request.get("logId");

        if (spirProjectId == null || logId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("status", "error", "message", "Invalid request parameters"));
        }

        try {
            com.example.project.service.InspireService.deleteInspireProject(spirProjectId, logId);
            inspireOmLinesRepository.updateProcessedStatus(spirProjectId);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Inspire project deleted successfully"));
        } catch (Exception e) {
            // handle business exceptions
            if (e instanceof BusinessException) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("status", "error", "Failed to delete Inspire project"));
            } else {
                // handle other exceptions
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("status", "error", "message", "Failed to delete Inspire project"));
            }
        }
    }
}