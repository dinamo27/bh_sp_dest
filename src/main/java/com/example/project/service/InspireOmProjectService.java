package com.example.project.service;

import com.example.project.entity.InspireOmLines;
import com.example.project.entity.InspireOmProject;
import com.example.project.entity.InspireLog;
import com.example.project.model.InspireLogUpdateRequest;
import com.example.project.model.InspireOmProjectUpdateRequest;
import com.example.project.repository.InspireOmProjectRepository;
import com.example.project.repository.InspireOmLinesRepository;
import com.example.project.repository.InspireLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class InspireOmProjectService {

    private final InspireOmProjectRepository inspireOmProjectRepository;
    private final InspireOmLinesRepository inspireOmLinesRepository;
    private final InspireLogRepository inspireLogRepository;

    @Autowired
    public InspireOmProjectService(InspireOmProjectRepository inspireOmProjectRepository, InspireOmLinesRepository inspireOmLinesRepository, InspireLogRepository inspireLogRepository) {
        this.inspireOmProjectRepository = inspireOmProjectRepository;
        this.inspireOmLinesRepository = inspireOmLinesRepository;
        this.inspireLogRepository = inspireLogRepository;
    }

    @Transactional
    public void updateInspireOmProject(Long spirProjectId, Long logId, String temp1, String temp2, String temp3, String temp4, String temp5) {
        try {
            Optional<InspireOmProject> projectOptional = inspireOmProjectRepository.findBySpirProjectId(spirProjectId);
            if (projectOptional.isPresent()) {
                InspireOmProject project = projectOptional.get();
                project.setProcessFlag("E");
                project.setErrorMessage("Forced om callback");
                inspireOmProjectRepository.save(project);

                Optional<InspireOmLines> linesOptional = inspireOmLinesRepository.findBySpirProjectId(spirProjectId);
                if (linesOptional.isPresent()) {
                    InspireOmLines lines = linesOptional.get();
                    lines.setProcessed("E");
                    inspireOmLinesRepository.save(lines);
                }

                addLogDetails(logId, spirProjectId, temp1, temp2, temp3, temp4, temp5);
                updateLogEntry(logId, "COMPLETED", "MESSAGE");
            }
        } catch (Exception e) {
            updateLogEntry(logId, "FAILED", "ERROR");
            throw e;
        }
    }

    @Cacheable("logDetails")
    public void addLogDetails(Long logId, Long spirProjectId, String temp1, String temp2, String temp3, String temp4, String temp5) {
        // implement addLogDetails logic
        // For demonstration purposes, assume a simple logging mechanism
        System.out.println("Logging details for logId: " + logId + ", spirProjectId: " + spirProjectId);
        System.out.println("Temp values: " + temp1 + ", " + temp2 + ", " + temp3 + ", " + temp4 + ", " + temp5);
    }

    public void updateLogEntry(Long logId, String message, String type) {
        Optional<InspireLog> logOptional = inspireLogRepository.findById(logId);
        if (logOptional.isPresent()) {
            InspireLog log = logOptional.get();
            log.setMessage(message);
            log.setType(type);
            inspireLogRepository.save(log);
        }
    }

    public void updateInspireProjectProc(Long spirProjectId) {
        // implement updateInspireProjectProc logic
        // For demonstration purposes, assume a simple procedure call
        System.out.println("Updating InspireProjectProc for spirProjectId: " + spirProjectId);
    }
}