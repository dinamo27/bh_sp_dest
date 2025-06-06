package com.example.project.controller.api;

import com.example.project.entity.InspireProject;
import com.example.project.entity.LogEntry;
import com.example.project.entity.request.InspireProjectRequest;
import com.example.project.service.InspireProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
public class InspireProjectController {

    private final InspireProjectService inspireProjectService;

    @Autowired
    public InspireProjectController(InspireProjectService inspireProjectService) {
        this.inspireProjectService = inspireProjectService;
    }

    @PostMapping("/update-inspire-project")
    public ResponseEntity<Map<String, Object>> updateInspireProject(@Valid @RequestBody InspireProjectRequest request) {
        Map<String, Object> response = new HashMap<>();

        if (request.getSpirProjectId() == null || request.getSpirProjectId().isEmpty() ||
                request.getLogId() == null || request.getLogId().isEmpty() ||
                request.getTemp1() == null || request.getTemp1().isEmpty() ||
                request.getTemp2() == null || request.getTemp2().isEmpty() ||
                request.getTemp3() == null || request.getTemp3().isEmpty() ||
                request.getTemp4() == null || request.getTemp4().isEmpty() ||
                request.getTemp5() == null || request.getTemp5().isEmpty()) {
            response.put("status", "error");
            response.put("message", "All fields are required");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            InspireProject inspireProject = inspireProjectService.updateInspireProject(request.getSpirProjectId(), request.getLogId(),
                    request.getTemp1(), request.getTemp2(), request.getTemp3(), request.getTemp4(), request.getTemp5());
            if (inspireProject == null) {
                response.put("status", "error");
                response.put("message", "Inspire project not found");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            response.put("status", "success");
            response.put("payload", inspireProject);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            inspireProjectService.handleError(e);
            response.put("status", "error");
            response.put("message", "An error occurred while updating the inspire project");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}