package com.example.project.controller;

import com.example.project.model.InspireOmProjectUpdateRequest;
import com.example.project.service.InspireLogService;
import com.example.project.service.InspireOmLinesService;
import com.example.project.service.InspireOmProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class InspireOmProjectController {

    private final InspireOmProjectService inspireOmProjectService;
    private final InspireOmLinesService inspireOmLinesService;
    private final InspireLogService inspireLogService;

    @Autowired
    public InspireOmProjectController(InspireOmProjectService inspireOmProjectService, InspireOmLinesService inspireOmLinesService, InspireLogService inspireLogService) {
        this.inspireOmProjectService = inspireOmProjectService;
        this.inspireOmLinesService = inspireOmLinesService;
        this.inspireLogService = inspireLogService;
    }

    @PreAuthorize("hasAuthority('UPDATE_INSPRIRE_OM_PROJECT')")
    @PutMapping("/inspire-om-project")
    public ResponseEntity<Void> updateInspireOmProject(@Valid @RequestBody InspireOmProjectUpdateRequest request) {
        try {
            inspireOmProjectService.updateInspireOmProject(request);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("hasAuthority('UPDATE_INSPRIRE_OM_PROJECT')")
    @PostMapping("/update-inspire-om-project")
    public ResponseEntity<Void> updateInspireOmProjectPost(@Valid @RequestBody InspireOmProjectUpdateRequest request) {
        try {
            inspireOmProjectService.updateInspireOmProject(request);
            inspireOmLinesService.updateInspireOmLines(request);
            inspireLogService.updateLogEntry(request.getLogId());
            inspireLogService.addLogDetails(request.getLogId(), request.getTemp1(), request.getTemp2(), request.getTemp3(), request.getTemp4(), request.getTemp5());
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}