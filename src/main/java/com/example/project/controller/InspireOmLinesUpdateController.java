package com.example.project.controller;

import com.example.project.dto.InspireOmLinesUpdateRequest;
import com.example.project.dto.InspireOmLinesUpdateResponse;
import com.example.project.service.InspireOmLinesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.logging.Logger;

@RestController
public class InspireOmLinesUpdateController {

    private final InspireOmLinesService inspireOmLinesService;
    private final Logger logger;

    @Autowired
    public InspireOmLinesUpdateController(InspireOmLinesService inspireOmLinesService, Logger logger) {
        this.inspireOmLinesService = inspireOmLinesService;
        this.logger = logger;
    }

    @PostMapping("/update-inspire-om-lines")
    @Transactional
    public ResponseEntity<InspireOmLinesUpdateResponse> updateInspireOmLines(@Valid @RequestBody InspireOmLinesUpdateRequest request) {
        try {
            if (request.getSpirProj() == null || request.getSpirProj().isEmpty() ||
                    request.getLogId() == null || request.getLogId().isEmpty() ||
                    request.getTemp1() == null || request.getTemp1().isEmpty() ||
                    request.getTemp2() == null || request.getTemp2().isEmpty() ||
                    request.getTemp3() == null || request.getTemp3().isEmpty() ||
                    request.getTemp4() == null || request.getTemp4().isEmpty() ||
                    request.getTemp5() == null || request.getTemp5().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new InspireOmLinesUpdateResponse(-1, "Invalid input parameters"));
            }

            inspireOmLinesService.updateInspireOmLines(request.getSpirProj(), request.getLogId(), request.getTemp1(), request.getTemp2(), request.getTemp3(), request.getTemp4(), request.getTemp5());
            return ResponseEntity.ok(new InspireOmLinesUpdateResponse(0, "Inspire OM Lines updated successfully"));
        } catch (Exception e) {
            logger.severe("Error updating Inspire OM Lines: " + e.getMessage());
            // Rollback the transaction
            // Update the project status
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new InspireOmLinesUpdateResponse(-1, "Error updating Inspire OM Lines"));
        }
    }
}