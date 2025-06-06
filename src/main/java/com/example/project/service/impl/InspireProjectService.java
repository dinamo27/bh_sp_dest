package com.example.project.service.impl;

import com.example.project.entity.InspireProject;
import com.example.project.entity.LogEntry;
import com.example.project.repository.InspireOMLinesRepository;
import com.example.project.repository.InspireOMProjectRepository;
import com.example.project.repository.InspireProjectRepository;
import com.example.project.repository.LogEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class InspireProjectService {

    private final InspireProjectRepository inspireProjectRepository;
    private final InspireOMProjectRepository inspireOMProjectRepository;
    private final InspireOMLinesRepository inspireOMLinesRepository;
    private final LogEntryRepository logEntryRepository;

    @Autowired
    public InspireProjectService(InspireProjectRepository inspireProjectRepository,
                                 InspireOMProjectRepository inspireOMProjectRepository,
                                 InspireOMLinesRepository inspireOMLinesRepository,
                                 LogEntryRepository logEntryRepository) {
        this.inspireProjectRepository = inspireProjectRepository;
        this.inspireOMProjectRepository = inspireOMProjectRepository;
        this.inspireOMLinesRepository = inspireOMLinesRepository;
        this.logEntryRepository = logEntryRepository;
    }

    @Transactional
    public Object updateInspireProject(Long spirProjectId, Long logId, String temp1, String temp2, String temp3, String temp4, String temp5) {
        try {
            if (spirProjectId == null || logId == null) {
                throw new Exception("spirProjectId and logId cannot be null");
            }
            inspireProjectRepository.updateProjectStatus(spirProjectId);
            inspireOMProjectRepository.updateOMProject(spirProjectId);
            inspireOMLinesRepository.updateOMLines(spirProjectId);
            logEntryRepository.updateLogEntry(logId, "COMPLETED");
            logEntryRepository.addLogDetails(logId, temp1, temp2, temp3, temp4, temp5);
            return new Object[]{true, ""};
        } catch (Exception e) {
            return handleError(spirProjectId, logId, e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Object handleError(Long spirProjectId, Long logId, String errorMessage) {
        try {
            logEntryRepository.updateLogEntry(logId, "ERROR");
            inspireProjectRepository.updateInspireProjectProcedure(spirProjectId, errorMessage);
            return new Object[]{-1, errorMessage};
        } catch (Exception e) {
            return new Object[]{-1, e.getMessage()};
        }
    }
}