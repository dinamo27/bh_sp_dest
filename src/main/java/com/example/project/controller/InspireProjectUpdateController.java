package com.example.project.controller;

import com.example.project.model.InspireProjectUpdateRequest;
import com.example.project.model.InspireProjectUpdateResponse;
import com.example.project.service.InspireOmProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InspireProjectUpdateController {

    private final InspireOmProjectService inspireOmProjectService;

    @Autowired
    public InspireProjectUpdateController(InspireOmProjectService inspireOmProjectService) {
        this.inspireOmProjectService = inspireOmProjectService;
    }

    @PostMapping("/update-inspire-project")
    public ResponseEntity<InspireProjectUpdateResponse> updateInspireProject(@RequestBody InspireProjectUpdateRequest request) {
        if (request.getSpirProjectId() == null || request.getSpirProjectId().isEmpty() ||
                request.getLogId() == null || request.getLogId().isEmpty() ||
                request.getTemp1() == null || request.getTemp1().isEmpty() ||
                request.getTemp2() == null || request.getTemp2().isEmpty() ||
                request.getTemp3() == null || request.getTemp3().isEmpty() ||
                request.getTemp4() == null || request.getTemp4().isEmpty() ||
                request.getTemp5() == null || request.getTemp5().isEmpty()) {
            return ResponseEntity.badRequest().body(new InspireProjectUpdateResponse(false, "Missing required fields"));
        }

        try {
            inspireOmProjectService.updateInspireOmProject(request.getSpirProjectId(), request.getLogId(), request.getTemp1(), request.getTemp2(), request.getTemp3(), request.getTemp4(), request.getTemp5());
            return ResponseEntity.ok(new InspireProjectUpdateResponse(true, "Project updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new InspireProjectUpdateResponse(false, e.getMessage()));
        }
    }
}