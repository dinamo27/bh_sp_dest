package com.example.project.controller;

import com.example.project.model.UpdateInspire1Request;
import com.example.project.service.InspireProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UpdateInspire1Controller {

    private final InspireProjectService inspireProjectService;

    public UpdateInspire1Controller(InspireProjectService inspireProjectService) {
        this.inspireProjectService = inspireProjectService;
    }

    @PostMapping("/update-project-status")
    public ResponseEntity updateProjectStatus(@Validated @RequestBody UpdateInspire1Request request) {
        try {
            inspireProjectService.updateProjectStatus(
                    request.getSpirProjectId(),
                    request.getLogId(),
                    request.getTemp1(),
                    request.getTemp2(),
                    request.getTemp3(),
                    request.getTemp4(),
                    request.getTemp5()
            );
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @ExceptionHandler(Exception.class)
    private ResponseEntity handleException(Exception e) {
        // Log error
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}