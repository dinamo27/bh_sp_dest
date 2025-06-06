package com.example.project.controller;

import com.example.project.model.InspireProjectStatusUpdateRequest;
import com.example.project.model.InspireProjectStatusUpdateResponse;
import com.example.project.service.InspireProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InspireProjectStatusController {

    private final InspireProjectService inspireProjectService;

    @Autowired
    public InspireProjectStatusController(InspireProjectService inspireProjectService) {
        this.inspireProjectService = inspireProjectService;
    }

    @PostMapping("/update-project-status")
    public ResponseEntity<InspireProjectStatusUpdateResponse> updateProjectStatus(@Validated @RequestBody InspireProjectStatusUpdateRequest request) {
        InspireProjectStatusUpdateResponse response = inspireProjectService.updateProjectStatus(request.getSpirProjectId(), request.getLogId(), request.getTemp1(), request.getTemp2(), request.getTemp3(), request.getTemp4(), request.getTemp5());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}