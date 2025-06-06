package com.example.project.controller;

import com.example.project.model.InspireProjectStatusUpdateRequest;
import com.example.project.model.InspireProjectUpdateRequest;
import com.example.project.model.UpdateInspire1Request;
import com.example.project.service.InspireProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class InspireProjectController {

    private final InspireProjectService inspireProjectService;

    @Autowired
    public InspireProjectController(InspireProjectService inspireProjectService) {
        this.inspireProjectService = inspireProjectService;
    }

    @PutMapping("/projects/{spirProjectId}/status")
    public ResponseEntity<String> updateProjectStatus(@PathVariable @Valid Long spirProjectId) {
        try {
            inspireProjectService.updateProjectStatus(spirProjectId);
            return ResponseEntity.ok("Project status updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating project status");
        }
    }

    @PostMapping("/update-inspire-project")
    public ResponseEntity<String> updateInspireProject(@Valid @RequestBody UpdateInspire1Request request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request");
        }
        try {
            inspireProjectService.updateProjectStatus(request.getSpirProjectId(), request.getLogId(), request.getTemp1(), request.getTemp2(), request.getTemp3(), request.getTemp4(), request.getTemp5());
            return ResponseEntity.ok("Project updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating project");
        }
    }
}