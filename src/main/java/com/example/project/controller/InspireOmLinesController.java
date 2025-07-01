package com.example.project.controller;

import com.example.project.dto.InspireOmLinesRequest;
import com.example.project.dto.InspireOmLinesResponse;
import com.example.project.service.InspireOmLinesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InspireOmLinesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(InspireOmLinesController.class);
    private final InspireOmLinesService inspireOmLinesService;

    public InspireOmLinesController(InspireOmLinesService inspireOmLinesService) {
        this.inspireOmLinesService = inspireOmLinesService;
    }

    @PostMapping(value = "/update-inspire-om-lines", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InspireOmLinesResponse> updateInspireOmLines(@RequestBody InspireOmLinesRequest request) {
        try {
            if (request.getSpirProj() == null || request.getLogId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            int successFlag = inspireOmLinesService.updateInspireOmLines(request.getSpirProj(), request.getLogId(), request.getTemp1(), request.getTemp2(), request.getTemp3(), request.getTemp4(), request.getTemp5());
            return ResponseEntity.ok(new InspireOmLinesResponse(successFlag));
        } catch (Exception e) {
            LOGGER.error("Error updating inspire OM lines", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}