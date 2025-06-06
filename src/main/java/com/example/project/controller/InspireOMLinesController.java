package com.example.project.controller;

import com.example.project.entity.InspireOMLinesRequest;
import com.example.project.service.InspireOMLinesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.logging.Logger;

@RestController
public class InspireOMLinesController {

    private static final Logger LOGGER = Logger.getLogger(InspireOMLinesController.class.getName());

    private final InspireOMLinesService inspireOMLinesService;

    @Autowired
    public InspireOMLinesController(InspireOMLinesService inspireOMLinesService) {
        this.inspireOMLinesService = inspireOMLinesService;
    }

    @PostMapping("/update-inspire-om-lines")
    @PreAuthorize("hasAuthority('UPDATE_INSPIRE_OM_LINES')")
    public ResponseEntity<Void> updateInspireOMLines(@Valid @RequestBody InspireOMLinesRequest request) {
        try {
            inspireOMLinesService.updateInspireOMLines(request.getSpirProjectId(), request.getLogId(), request.getTemp1(), request.getTemp2(), request.getTemp3(), request.getTemp4(), request.getTemp5());
            return ResponseEntity.ok().build();
        } catch (ValidationException e) {
            LOGGER.warning("Validation failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (NotFoundException e) {
            LOGGER.warning("Not found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            LOGGER.severe("Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}