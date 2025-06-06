package com.example.project.controller.api;

import com.example.project.entity.request.InspireOMProjectRequest;
import com.example.project.service.InspireOMProjectService;
import com.example.project.service.InspireProjectService;
import com.example.project.repository.LogEntryRepository;
import com.example.project.entity.LogEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.logging.Logger;

@RestController
public class InspireOMProjectController {

    private final InspireOMProjectService inspireOMProjectService;
    private final InspireProjectService inspireProjectService;
    private final LogEntryRepository logEntryRepository;
    private final Logger logger;

    @Autowired
    public InspireOMProjectController(InspireOMProjectService inspireOMProjectService, InspireProjectService inspireProjectService, LogEntryRepository logEntryRepository) {
        this.inspireOMProjectService = inspireOMProjectService;
        this.inspireProjectService = inspireProjectService;
        this.logEntryRepository = logEntryRepository;
        this.logger = Logger.getLogger(InspireOMProjectController.class.getName());
    }

    @PutMapping("/updateInspireOMProject/{spirProjectId}/{logId}")
    public ResponseEntity<Void> updateInspireOMProject(@PathVariable Long spirProjectId, @PathVariable Long logId, @Valid @RequestBody InspireOMProjectRequest request) {
        try {
            inspireOMProjectService.updateInspireOMProject(spirProjectId, logId, request);
            logger.info("Update Inspire OM project completed for logId: " + logId);
            logger.info("Update details: logId=" + logId + ", spirProjectId=" + spirProjectId + ", temp1=" + request.getTemp1() + ", temp2=" + request.getTemp2() + ", temp3=" + request.getTemp3() + ", temp4=" + request.getTemp4() + ", temp5=" + request.getTemp5() + ", @@ROWCOUNT=" + logEntryRepository.getRowCount());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/update-inspire-project-status")
    public ResponseEntity<Object> updateInspireProjectStatus(@Valid @RequestBody InspireOMProjectRequest request) {
        try {
            int successFlag = inspireProjectService.updateInspireProject(request.getSpirProjectId(), request.getLogId(), request.getTemp1(), request.getTemp2(), request.getTemp3(), request.getTemp4(), request.getTemp5());
            String errorMessage = successFlag == 0 ? "Success" : "Error";
            return ResponseEntity.ok().body(new Object[]{successFlag, errorMessage});
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/update-inspire-om-project")
    public ResponseEntity<Void> updateInspireOMProject(@Validated @RequestBody InspireOMProjectRequest request) {
        try {
            inspireOMProjectService.updateInspireOMProject(request.getSpirProjectId(), request.getLogId(), request);
            logger.info("Update Inspire OM project completed for logId: " + request.getLogId());
            logger.info("Update details: logId=" + request.getLogId() + ", spirProjectId=" + request.getSpirProjectId() + ", temp1=" + request.getTemp1() + ", temp2=" + request.getTemp2() + ", temp3=" + request.getTemp3() + ", temp4=" + request.getTemp4() + ", temp5=" + request.getTemp5() + ", @@ROWCOUNT=" + logEntryRepository.getRowCount());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}