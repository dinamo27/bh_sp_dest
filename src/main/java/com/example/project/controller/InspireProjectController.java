package com.example.project.controller;

import com.example.project.entity.InspireOMProjectRequest;
import com.example.project.entity.LogEntry;
import com.example.project.repository.LogEntryRepository;
import com.example.project.service.InspireProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class InspireProjectController {

    private final InspireProjectService inspireProjectService;
    private final LogEntryRepository logEntryRepository;

    @Autowired
    public InspireProjectController(InspireProjectService inspireProjectService, LogEntryRepository logEntryRepository) {
        this.inspireProjectService = inspireProjectService;
        this.logEntryRepository = logEntryRepository;
    }

    @PutMapping("/updateInspireProject/{spirProjectId}/{logId}")
    public ResponseEntity<InspireOMProjectResponse> updateInspireProject(@PathVariable Long spirProjectId, @PathVariable Long logId) {
        if (spirProjectId == null || logId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new InspireOMProjectResponse(-1, "Invalid spirProjectId or logId"));
        }
        try {
            InspireOMProjectResponse response = inspireProjectService.updateInspireProject(spirProjectId, logId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logEntryRepository.updateLogEntry(logId, "Error updating project: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new InspireOMProjectResponse(-1, "Error updating project"));
        }
    }

    @PostMapping("/updateProjectStatus")
    public ResponseEntity<InspireOMProjectResponse> updateProjectStatus(@Valid @RequestBody InspireOMProjectRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new InspireOMProjectResponse(-1, "Invalid request"));
        }
        try {
            InspireOMProjectResponse response = inspireProjectService.updateInspireProject(request.getSpirProjectId(), request.getLogId(), request.getTemp1(), request.getTemp2(), request.getTemp3(), request.getTemp4(), request.getTemp5());
            return ResponseEntity.ok(response);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new InspireOMProjectResponse(-1, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new InspireOMProjectResponse(-1, "Error updating project status"));
        }
    }
}

class InspireOMProjectResponse {
    private int successFlag;
    private String returnMessage;

    public InspireOMProjectResponse(int successFlag, String returnMessage) {
        this.successFlag = successFlag;
        this.returnMessage = returnMessage;
    }

    public int getSuccessFlag() {
        return successFlag;
    }

    public void setSuccessFlag(int successFlag) {
        this.successFlag = successFlag;
    }

    public String getReturnMessage() {
        return returnMessage;
    }

    public void setReturnMessage(String returnMessage) {
        this.returnMessage = returnMessage;
    }
}

class BusinessException extends Exception {
    public BusinessException(String message) {
        super(message);
    }
}