package com.example.project.controller;

import com.example.project.model.UpdateInspire1Request;
import com.example.project.service.InspireOmLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InspireOmLinesController {

    private final InspireOmLineService inspireOmLineService;

    @Autowired
    public InspireOmLinesController(InspireOmLineService inspireOmLineService) {
        this.inspireOmLineService = inspireOmLineService;
    }

    @PostMapping("/update-inspire-om-lines")
    public ResponseEntity<?> updateInspireOmLines(@RequestBody UpdateInspire1Request request) {
        try {
            if (request.getSpirProjectId() == null || request.getSpirProjectId().isEmpty() ||
                    request.getLogId() == null || request.getLogId().isEmpty() ||
                    request.getTemp1() == null || request.getTemp1().isEmpty() ||
                    request.getTemp2() == null || request.getTemp2().isEmpty() ||
                    request.getTemp3() == null || request.getTemp3().isEmpty() ||
                    request.getTemp4() == null || request.getTemp4().isEmpty() ||
                    request.getTemp5() == null || request.getTemp5().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Input parameters cannot be null or empty");
            }
            inspireOmLineService.updateInspireOmLines(request.getSpirProjectId(), request.getLogId(), request.getTemp1(), request.getTemp2(), request.getTemp3(), request.getTemp4(), request.getTemp5());
            return ResponseEntity.status(HttpStatus.OK).body("Inspire OM Lines updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating Inspire OM Lines: " + e.getMessage());
        }
    }
}