package com.example.project.service;

import com.example.project.entity.InspireOmLine;
import com.example.project.entity.InspireOmProject;
import com.example.project.entity.InspireOmLines;
import com.example.project.entity.InspireProject;
import com.example.project.entity.LogEntry;
import com.example.project.repository.InspireOmLineRepository;
import com.example.project.repository.InspireOmLinesRepository;
import com.example.project.repository.InspireOmProjectRepository;
import com.example.project.repository.InspireProjectRepository;
import com.example.project.repository.LogEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.validation.Valid;

@Service
public class InspireProjectService {

    private final InspireOmLineRepository inspireOmLineRepository;
    private final InspireOmProjectRepository inspireOmProjectRepository;
    private final InspireProjectRepository inspireProjectRepository;
    private final InspireOmLinesRepository inspireOmLinesRepository;
    private final LogEntryRepository logEntryRepository;
    private final PlatformTransactionManager transactionManager;

    @Autowired
    public InspireProjectService(InspireOmLineRepository inspireOmLineRepository,
                                 InspireOmProjectRepository inspireOmProjectRepository,
                                 InspireProjectRepository inspireProjectRepository,
                                 InspireOmLinesRepository inspireOmLinesRepository,
                                 LogEntryRepository logEntryRepository,
                                 PlatformTransactionManager transactionManager) {
        this.inspireOmLineRepository = inspireOmLineRepository;
        this.inspireOmProjectRepository = inspireOmProjectRepository;
        this.inspireProjectRepository = inspireProjectRepository;
        this.inspireOmLinesRepository = inspireOmLinesRepository;
        this.logEntryRepository = logEntryRepository;
        this.transactionManager = transactionManager;
    }

    public void updateProjectStatus(Long spirProjectId) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);
        int successFlag = 0;

        try {
            inspireProjectRepository.updateProjectStatus(spirProjectId);
            inspireOmProjectRepository.updateOmProject(spirProjectId);
            inspireOmLinesRepository.updateOmLines(spirProjectId);
            logEntryRepository.updateLogEntry(null, "COMPLETED", "MESSAGE");
            successFlag = 0;
            transactionManager.commit(status);
        } catch (Exception e) {
            try {
                transactionManager.rollback(status);
            } catch (Exception ex) {
                // handle rollback exception
            }
            successFlag = -1;
            logEntryRepository.updateLogEntry(null, e.getMessage(), "ERROR");
        }
    }

    public void updateProjectStatus(@Valid Long spirProjectId, Long logId, String temp1, String temp2, String temp3, String temp4, String temp5) {
        if (spirProjectId == null || logId == null) {
            throw new IllegalArgumentException("spirProjectId and logId are required");
        }
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);

        try {
            inspireProjectRepository.updateProjectStatus(spirProjectId);
            inspireOmProjectRepository.updateOmProject(spirProjectId);
            inspireOmLinesRepository.updateOmLines(spirProjectId);
            logEntryRepository.updateLogEntry(logId, "UPDATE_SUCCESS");
            transactionManager.commit(status);
        } catch (Exception e) {
            try {
                transactionManager.rollback(status);
            } catch (Exception ex) {
                // handle rollback exception
            }
            logEntryRepository.updateLogEntry(logId, "UPDATE_FAILURE");
            throw new RuntimeException("Error updating project status", e);
        }
    }

    public void updateOmProject(Long spirProjectId, String processFlag, String errorMessage) {
        inspireOmProjectRepository.updateOmProject(spirProjectId, processFlag, errorMessage);
        logEntryRepository.updateLogEntry(null, processFlag, errorMessage);
    }
}