package com.example.project.controller;

import com.example.project.entity.InspireProject;
import com.example.project.service.InspireProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InspireProjectUpdateController {

    private final InspireProjectService inspireProjectService;

    @Autowired
    public InspireProjectUpdateController(InspireProjectService inspireProjectService) {
        this.inspireProjectService = inspireProjectService;
    }

    @PostMapping("/update-inspire-project/{spirProjectId}")
    public ResponseEntity<InspireProjectResponse> updateInspireProject(@PathVariable Long spirProjectId) {
        try {
            InspireProject updatedProject = inspireProjectService.updateInspireProject(spirProjectId);
            InspireProjectResponse response = new InspireProjectResponse(HttpStatus.OK.value(), "Project updated successfully", updatedProject);
            return ResponseEntity.ok(response);
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new InspireProjectResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new InspireProjectResponse(HttpStatus.CONFLICT.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new InspireProjectResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error", null));
        }
    }
}

class InspireProjectResponse {
    private int status;
    private String message;
    private InspireProject payload;

    public InspireProjectResponse(int status, String message, InspireProject payload) {
        this.status = status;
        this.message = message;
        this.payload = payload;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public InspireProject getPayload() {
        return payload;
    }
}