

package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.example.model.Log;
import com.example.model.ProjectId;
import com.example.repository.InspireOmLinesRepository;
import com.example.repository.InspireOmProjectRepository;
import com.example.repository.InspirePartsGroupedRecalcRepository;
import com.example.repository.InspirePositionToActivityTempRepository;
import com.example.repository.InspireProjectRepository;
import com.example.repository.InspireSpirRefreshDataRepository;
import com.example.service.LogService;

@Service
public class ResetProjectService {

    @Autowired
    private LogService logService;

    @Autowired
    private InspirePartsGroupedRecalcRepository inspirePartsGroupedRecalcRepository;

    @Autowired
    private InspirePositionToActivityTempRepository inspirePositionToActivityTempRepository;

    @Autowired
    private InspireSpirRefreshDataRepository inspireSpirRefreshDataRepository;

    @Autowired
    private InspireProjectRepository inspireProjectRepository;

    @Autowired
    private InspireOmProjectRepository inspireOmProjectRepository;

    @Autowired
    private InspireOmLinesRepository inspireOmLinesRepository;

    @Transactional
    public int resetProject(ProjectId projectId) {
        if (projectId == null) {
            return -1;
        }

        Log log = logService.createLogEntry("tech_reset_project", projectId);

        try {
            inspirePartsGroupedRecalcRepository.updateProcessed(projectId, "Y");

            inspirePositionToActivityTempRepository.deleteBySpirProjectId(projectId);

            inspireSpirRefreshDataRepository.updateProcessed(projectId, "Y");

            inspireProjectRepository.updateSpirStatus(projectId, "COMPLETED");

            inspireOmProjectRepository.updateProcessFlagAndErrorMessage(projectId, "E", "Forced om callback");

            inspireOmLinesRepository.updateProcessed(projectId, "E");

            logService.logOutcome(log, "COMPLETED", "MESSAGE");

            return 0;
        } catch (Exception e) {
            logService.logOutcome(log, "FAILED", e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return -1;
        }
    }
}