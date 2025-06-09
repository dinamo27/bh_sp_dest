package com.example.project.controller;

import com.example.project.model.InspireLogUpdateRequest;
import com.example.project.model.InspireProjectStatusUpdateRequest;
import com.example.project.service.InspireLogService;
import com.example.project.service.InspireOmProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class InspireProjectStatusController {

    private final InspireOmProjectService inspireOmProjectService;
    private final InspireLogService inspireLogService;

    @Autowired
    public InspireProjectStatusController(InspireOmProjectService inspireOmProjectService, InspireLogService inspireLogService) {
        this.inspireOmProjectService = inspireOmProjectService;
        this.inspireLogService = inspireLogService;
    }

    @PostMapping("/update-project-status")
    public ResponseEntity<String> updateProjectStatus(@Valid @RequestBody InspireProjectStatusUpdateRequest request) {
        try {
            inspireOmProjectService.updateInspireOmProject(request);
            return ResponseEntity.ok("Project status updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating project status: " + e.getMessage());
        }
    }

    @PostMapping("/update-log-entry")
    public ResponseEntity<String> updateLogEntry(@Valid @RequestBody InspireLogUpdateRequest request) {
        try {
            inspireLogService.updateLogEntry(request);
            return ResponseEntity.ok("Log entry updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating log entry: " + e.getMessage());
        }
    }
}