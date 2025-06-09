package com.example.project.service;

import com.example.project.entity.InspireLog;
import com.example.project.model.InspireLogUpdateRequest;
import com.example.project.repository.InspireLogRepository;
import com.example.project.service.InspireOmLinesService;
import org.springframework.stereotype.Service;

@Service
public class InspireProjectProcService {

    private final InspireOmLinesService inspireOmLinesService;
    private final InspireLogRepository inspireLogRepository;

    public InspireProjectProcService(InspireOmLinesService inspireOmLinesService, InspireLogRepository inspireLogRepository) {
        this.inspireOmLinesService = inspireOmLinesService;
        this.inspireLogRepository = inspireLogRepository;
    }

    public void updateInspireProjectProc(Long spirProjectId) {
        if (spirProjectId == null) {
            throw new IllegalArgumentException("spirProjectId cannot be null");
        }

        try {
            inspireOmLinesService.updateInspireOmLines(spirProjectId);
        } catch (Exception e) {
            InspireLog log = inspireLogRepository.findById(spirProjectId).orElseThrow(() -> new RuntimeException("Log not found for spirProjectId " + spirProjectId));
            InspireLogUpdateRequest logUpdateRequest = new InspireLogUpdateRequest(log.getLogId(), e.getMessage(), "ERROR");
            inspireOmLinesService.updateLogEntry(logUpdateRequest);
            try {
                try {
                inspireOmLinesService.updateInspireProjectProc(spirProjectId);
            } catch (Exception ex) {
                throw new RuntimeException("Failed to update InspireProjectProc", ex);
            }
        }
        try {
            inspireOmLinesService.updateInspireProjectProc(spirProjectId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update InspireProjectProc", e);
        }
    }
}