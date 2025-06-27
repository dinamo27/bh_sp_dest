package com.example.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.project.model.ResetResult;
import com.example.project.repository.InspireOmLineRepository;
import com.example.project.repository.InspireOmProjectRepository;

@Service
public class InspireProjectResetServiceImpl implements InspireProjectResetService {

    private final InspireOmProjectRepository inspireOmProjectRepository;
    private final InspireOmLineRepository inspireOmLineRepository;
    private final LoggingService loggingService;

    @Autowired
    public InspireProjectResetServiceImpl(
            InspireOmProjectRepository inspireOmProjectRepository,
            InspireOmLineRepository inspireOmLineRepository,
            LoggingService loggingService) {
        this.inspireOmProjectRepository = inspireOmProjectRepository;
        this.inspireOmLineRepository = inspireOmLineRepository;
        this.loggingService = loggingService;
    }

    @Override
    @Transactional
    public ResetResult resetInspireOmProject(Long spirProjectId, Long logId) {
        int success = 0;
        String returnMessage = null;
        int temp1 = 0, temp2 = 0, temp3 = 0, temp4 = 0, temp5 = 0;
        int rowCount = 0;
        
        try {
            // Update pending projects to error status
            temp5 = inspireOmProjectRepository.updatePendingProjectsToError(spirProjectId, "Forced om callback");
            
            // Update pending lines to error status
            rowCount = inspireOmLineRepository.updatePendingLinesToError(spirProjectId);
            
            // Update log entry to completed status
            loggingService.updateLogEntry(logId, "COMPLETED", "MESSAGE");
            
            // Add log details with counters
            loggingService.addLogDetails(logId, spirProjectId, temp1, temp2, temp3, temp4, temp5, rowCount);
            
        } catch (Exception e) {
            // Set error flag and message
            success = -1;
            returnMessage = e.getMessage();
            
            // Update log with error
            loggingService.updateLogEntry(logId, "ERROR", returnMessage);
            
            // Update project processing status
            updateInspireProjectProc(spirProjectId);
        }
        
        // Return result
        return new ResetResult(success, returnMessage);
    }
    
    private void updateInspireProjectProc(Long spirProjectId) {
        // Implementation for updating project processing status when an error occurs
        // This handles any necessary cleanup or status updates for the project
        try {
            inspireOmProjectRepository.updatePendingProjectsToError(spirProjectId, "Error during reset operation");
        } catch (Exception e) {
            // If this fails, we've already caught an exception in the main method,
            // so we don't need to propagate this one
        }
    }
}