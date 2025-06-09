package com.example.project.controller;

import com.example.project.model.InspireOmLinesUpdateRequest;
import com.example.project.service.InspireOmLinesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
public class InspireOmLinesController {

    private final InspireOmLinesService inspireOmLinesService;

    @Autowired
    public InspireOmLinesController(InspireOmLinesService inspireOmLinesService) {
        this.inspireOmLinesService = inspireOmLinesService;
    }

    @PostMapping("/update-inspire-om-lines")
    public ResponseEntity<?> updateInspireOmLines(@Valid @RequestBody InspireOmLinesUpdateRequest request) {
        try {
            if (request.getSpirProjectId() == null || request.getLogId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("spirProjectId and logId are required");
            }
            inspireOmLinesService.updateInspireOmLines(request.getSpirProjectId(), request.getLogId(), request.getTemp1(), request.getTemp2(), request.getTemp3(), request.getTemp4(), request.getTemp5());
            return ResponseEntity.status(HttpStatus.OK).body("Inspire OM Lines updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating Inspire OM Lines");
        }
    }

    @PutMapping("/inspire-om-lines")
    public ResponseEntity<?> updateInspireOmLinesPut(@Validated @RequestBody InspireOmLinesUpdateRequest request) {
        try {
            if (request.getSpirProjectId() == null || request.getLogId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("spirProjectId and logId are required");
            }
            inspireOmLinesService.updateInspireOmLines(request.getSpirProjectId(), request.getLogId(), request.getTemp1(), request.getTemp2(), request.getTemp3(), request.getTemp4(), request.getTemp5());
            return ResponseEntity.status(HttpStatus.OK).body("Inspire OM Lines updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating Inspire OM Lines");
        }
    }
}