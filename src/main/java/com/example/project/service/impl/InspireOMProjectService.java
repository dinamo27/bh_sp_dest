package com.example.project.service.impl;

import com.example.project.entity.InspireOMProject;
import com.example.project.entity.InspireOMProjectRequest;
import com.example.project.entity.LogEntry;
import com.example.project.repository.InspireOMProjectRepository;
import com.example.project.repository.InspireProjectRepository;
import com.example.project.repository.InspireSpirRefreshDataRepository;
import com.example.project.repository.LogEntryRepository;
import com.example.project.service.InspireOMLinesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.logging.Logger;

@Service
public class InspireOMProjectService {

    private static final Logger logger = Logger.getLogger(InspireOMProjectService.class.getName());

    private final InspireOMProjectRepository inspireOMProjectRepository;
    private final InspireProjectRepository inspireProjectRepository;
    private final InspireSpirRefreshDataRepository inspireSpirRefreshDataRepository;
    private final LogEntryRepository logEntryRepository;
    private final InspireOMLinesService inspireOMLinesService;

    @Autowired
    public InspireOMProjectService(InspireOMProjectRepository inspireOMProjectRepository,
                                   InspireProjectRepository inspireProjectRepository,
                                   InspireSpirRefreshDataRepository inspireSpirRefreshDataRepository,
                                   LogEntryRepository logEntryRepository,
                                   InspireOMLinesService inspireOMLinesService) {
        this.inspireOMProjectRepository = inspireOMProjectRepository;
        this.inspireProjectRepository = inspireProjectRepository;
        this.inspireSpirRefreshDataRepository = inspireSpirRefreshDataRepository;
        this.logEntryRepository = logEntryRepository;
        this.inspireOMLinesService = inspireOMLinesService;
    }

    @Transactional
    public void updateInspireOMProject(Long spirProjectId, Long logId, String temp1, String temp2, String temp3, String temp4, String temp5) {
        if (spirProjectId != null && logId != null) {
            try {
                inspireOMProjectRepository.updateProcessFlagBySpirProjectId(spirProjectId, 'E');
                inspireOMProjectRepository.updateErrorMessageBySpirProjectId(spirProjectId, "Forced om callback");
                inspireOMLinesService.updateInspireOMLines(spirProjectId, 'E');
                logEntryRepository.updateLogEntry(logId, "COMPLETED", "MESSAGE");
                logEntryRepository.addLogDetails(logId, spirProjectId, temp1, temp2, temp3, temp4, temp5, null, null, null);
            } catch (Exception e) {
                handleError(logId, e.getMessage());
            }
        } else {
            logger.severe("Invalid input parameters");
        }
    }

    @Transactional
    public void handleError(Long logId, String errorMessage) {
        try {
            logEntryRepository.updateLogEntry(logId, "ERROR", errorMessage);
            inspireProjectRepository.updateInspireProjectProc(logId);
        } catch (Exception e) {
            logger.severe("Error occurred while handling error: " + e.getMessage());
        }
    }

    @Transactional
    public void updateInspireOMProject(InspireOMProjectRequest request) {
        if (request != null) {
            Long spirProjectId = request.getSpirProjectId();
            Long logId = request.getLogId();
            if (spirProjectId != null && logId != null) {
                try {
                    inspireOMProjectRepository.updateProcessFlagBySpirProjectId(spirProjectId, 'E');
                    inspireOMProjectRepository.updateErrorMessageBySpirProjectId(spirProjectId, "Forced om callback");
                    inspireOMLinesService.updateInspireOMLines(spirProjectId, 'E');
                    logEntryRepository.updateLogEntry(logId, "COMPLETED", "MESSAGE");
                    logEntryRepository.addLogDetails(logId, spirProjectId, request.getTemp1(), request.getTemp2(), request.getTemp3(), request.getTemp4(), request.getTemp5(), null, null, null);
                } catch (Exception e) {
                    handleError(logId, e.getMessage());
                }
            } else {
                logger.severe("Invalid input parameters");
            }
        } else {
            logger.severe("Invalid input parameters");
        }
    }

    @Transactional
    public void updateInspireOMProject(Long spirProjectId, Long logId, InspireOMProjectRequest request) {
        if (spirProjectId != null && logId != null && request != null) {
            try {
                inspireOMProjectRepository.updateOMProject(spirProjectId);
                inspireProjectRepository.updateProjectStatus(spirProjectId);
                inspireOMLinesService.updateOMLines(spirProjectId);
                logEntryRepository.updateLogEntry(logId);
            } catch (Exception e) {
                handleError(logId, e.getMessage());
            }
        } else {
            logger.severe("Invalid input parameters");
        }
    }
}