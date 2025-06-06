package com.example.project.controller;

import com.example.project.entity.request.InspireOMProjectRequest;
import com.example.project.service.InspireOMProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
public class InspireOMProjectController {

    private final InspireOMProjectService inspireOMProjectService;

    @Autowired
    public InspireOMProjectController(InspireOMProjectService inspireOMProjectService) {
        this.inspireOMProjectService = inspireOMProjectService;
    }

    @PostMapping("/update-inspire-om-project")
    public ResponseEntity<Void> updateInspireOMProject(@Valid @RequestBody InspireOMProjectRequest request) {
        if (request.getSpirProjectId() == null || request.getLogId() == null || request.getTemp1() == null || request.getTemp2() == null || request.getTemp3() == null || request.getTemp4() == null || request.getTemp5() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            inspireOMProjectService.updateInspireOMProject(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}