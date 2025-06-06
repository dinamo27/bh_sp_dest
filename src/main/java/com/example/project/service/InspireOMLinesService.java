package com.example.project.service;

import com.example.project.entity.InspireOMLines;
import com.example.project.entity.InspireOMLinesRequest;
import com.example.project.entity.LogEntry;
import com.example.project.repository.dao.InspireOMLinesRepository;
import com.example.project.repository.LogEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.transaction.TransactionStatus;

import java.util.List;

@Service
public class InspireOMLinesService {

    private final InspireOMLinesRepository inspireOMLinesRepository;
    private final LogEntryRepository logEntryRepository;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public InspireOMLinesService(InspireOMLinesRepository inspireOMLinesRepository, LogEntryRepository logEntryRepository, TransactionTemplate transactionTemplate) {
        this.inspireOMLinesRepository = inspireOMLinesRepository;
        this.logEntryRepository = logEntryRepository;
        this.transactionTemplate = transactionTemplate;
    }

    @Transactional
    public void updateInspireOMLines(Long spirProjectId, Long logId, InspireOMLinesRequest request) {
        if (spirProjectId == null || logId == null || request == null) {
            throw new IllegalArgumentException("Invalid input");
        }

        List<InspireOMLines> inspireOMLinesList = inspireOMLinesRepository.findBySpirProjectIdAndProcessed(spirProjectId, 'P');
        if (inspireOMLinesList.isEmpty()) {
            throw new IllegalArgumentException("No records found for spirProjectId " + spirProjectId);
        }

        inspireOMLinesList.forEach(inspireOMLines -> inspireOMLines.setProcessed('E'));
        inspireOMLinesRepository.saveAll(inspireOMLinesList);

        LogEntry logEntry = logEntryRepository.findById(logId).orElseThrow(() -> new IllegalArgumentException("Log entry not found for logId " + logId));
        logEntry.setOutcome("Updated inspire OM lines");
        logEntry.addLogDetails("Updated " + inspireOMLinesList.size() + " records");
        logEntryRepository.save(logEntry);

        transactionTemplate.execute(status -> {
            status.flush();
            return null;
        });
    }

    public void handleError(Throwable throwable) {
        transactionTemplate.execute(status -> {
            status.setRollbackOnly();
            return null;
        });

        // Log error using logging mechanism
        System.err.println("Error occurred: " + throwable.getMessage());

        // Update inspire_project_proc procedure with error message
        // Assuming a method to update the procedure
        // inspireProjectProcRepository.updateWithError(throwable.getMessage());
    }
}