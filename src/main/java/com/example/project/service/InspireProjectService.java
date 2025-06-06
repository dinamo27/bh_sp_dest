package com.example.project.service;

import com.example.project.entity.InspireSpirRefreshData;
import com.example.project.entity.InspireProject;
import com.example.project.entity.InspireOMProject;
import com.example.project.entity.InspireOMLines;
import com.example.project.entity.LogEntry;
import com.example.project.repository.InspireSpirRefreshDataRepository;
import com.example.project.repository.InspireProjectRepository;
import com.example.project.repository.InspireOMProjectRepository;
import com.example.project.repository.InspireOMLinesRepository;
import com.example.project.repository.LogEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Service
public class InspireProjectService {

    private final InspireSpirRefreshDataRepository inspireSpirRefreshDataRepository;
    private final InspireProjectRepository inspireProjectRepository;
    private final InspireOMProjectRepository inspireOMProjectRepository;
    private final InspireOMLinesRepository inspireOMLinesRepository;
    private final LogEntryRepository logEntryRepository;
    private final PlatformTransactionManager transactionManager;

    @Autowired
    public InspireProjectService(InspireSpirRefreshDataRepository inspireSpirRefreshDataRepository,
                                 InspireProjectRepository inspireProjectRepository,
                                 InspireOMProjectRepository inspireOMProjectRepository,
                                 InspireOMLinesRepository inspireOMLinesRepository,
                                 LogEntryRepository logEntryRepository,
                                 PlatformTransactionManager transactionManager) {
        this.inspireSpirRefreshDataRepository = inspireSpirRefreshDataRepository;
        this.inspireProjectRepository = inspireProjectRepository;
        this.inspireOMProjectRepository = inspireOMProjectRepository;
        this.inspireOMLinesRepository = inspireOMLinesRepository;
        this.logEntryRepository = logEntryRepository;
        this.transactionManager = transactionManager;
    }

    public void updateInspireProject(Long spirProjectId, Long logId) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);
        int successFlag = 0;
        String returnMessage = "";

        try {
            inspireSpirRefreshDataRepository.updateProcessedBySpirProjectId(spirProjectId);
            inspireProjectRepository.updateProjectStatus(spirProjectId);
            inspireOMProjectRepository.updateOMProject(spirProjectId);
            inspireOMLinesRepository.updateOMLines(spirProjectId);
            logEntryRepository.updateLogEntry(logId, "COMPLETED", "MESSAGE");
            successFlag = 0;
            transactionManager.commit(status);
        } catch (Exception e) {
            try {
                transactionManager.rollback(status);
            } catch (Exception ex) {
                // handle rollback exception
            }
            successFlag = -1;
            returnMessage = e.getMessage();
            logEntryRepository.updateLogEntry(logId, returnMessage, "ERROR");
        }
    }
}