package com.example.project.controller;

import com.example.project.model.DeleteInspireSpirRefreshDataRequest;
import com.example.project.model.InspireProjectUpdateRequest;
import com.example.project.service.InspireProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InspireProjectUpdateController {

    private final InspireProjectService inspireProjectService;

    public InspireProjectUpdateController(InspireProjectService inspireProjectService) {
        this.inspireProjectService = inspireProjectService;
    }

    @PostMapping("/update-inspire-project")
    public ResponseEntity<InspireProjectUpdateRequest>> public ResponseEntity<InspireProjectUpdateRequest> updateInspireProject(@RequestBody DeleteInspireSpirRefreshDataRequest request) {
        try {
            if (request.getSpirProjectId() == null || request.getSpirProjectId().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new InspireProjectUpdateRequest(HttpStatus.BAD_REQUEST.value(), "spirProjectId cannot be null or empty", null));
            }
            InspireProjectUpdateRequest response = inspireProjectService.updateProjectStatus(request.getSpirProjectId());
            return ResponseEntity.ok(response);
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new InspireProjectUpdateRequest(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new InspireProjectUpdateRequest(HttpStatus.CONFLICT.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new InspireProjectUpdateRequest(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error", null));
        }
    }
}