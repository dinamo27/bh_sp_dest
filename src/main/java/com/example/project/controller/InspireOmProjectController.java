package com.example.project.controller;

import com.example.project.model.InspireOmProjectUpdateRequest;
import com.example.project.service.InspireOmProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InspireOmProjectController {

    private final InspireOmProjectService inspireOmProjectService;

    public InspireOmProjectController(InspireOmProjectService inspireOmProjectService) {
        this.inspireOmProjectService = inspireOmProjectService;
    }

    @PostMapping("/update-inspire-om-project")
    public ResponseEntity<?> updateInspireOmProject(@RequestBody InspireOmProjectUpdateRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(inspireOmProjectService.updateInspireOmProject(request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating Inspire Om Project: " + e.getMessage());
        }
    }

    // Note: The HTTP method and URL path mapping for the updateOmProject endpoint are not specified in the solution design.
    // Therefore, I will assume a POST request to /update-om-project for this example.
    // Please update the HTTP method and URL path mapping according to your requirements.

    // @PostMapping("/update-om-project")
    // public ResponseEntity<?> updateOmProject(@RequestBody Long spirProjectId) {
    //     try {
    //         return ResponseEntity.status(HttpStatus.OK).body(inspireOmProjectService.updateOmProject(spirProjectId));
    //     } catch (Exception e) {
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating OM Project: " + e.getMessage());
    //     }
    // }
}