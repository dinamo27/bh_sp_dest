package com.example.project.service;

import com.example.project.entity.InspireOmLine;
import com.example.project.entity.InspireOmProject;
import com.example.project.entity.LogEntry;
import com.example.project.repository.InspireOmLineRepository;
import com.example.project.repository.InspireOmLinesRepository;
import com.example.project.repository.InspireOmProjectRepository;
import com.example.project.repository.LogEntryRepository;
import com.example.project.service.LogEntryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class InspireOmProjectService {

    private final InspireOmLineRepository inspireOmLineRepository;
    private final InspireOmLineService inspireOmLineService;
    private final InspireOmProjectRepository inspireOmProjectRepository;
    private final InspireOmLinesRepository inspireOmLinesRepository;
    private final LogEntryRepository logEntryRepository;
    private final LogEntryService logEntryService;

    @PersistenceContext
    private EntityManager entityManager;

    public InspireOmProjectService(InspireOmLineRepository inspireOmLineRepository,
                                   InspireOmLineService inspireOmLineService,
                                   InspireOmProjectRepository inspireOmProjectRepository,
                                   InspireOmLinesRepository inspireOmLinesRepository,
                                   LogEntryRepository logEntryRepository,
                                   LogEntryService logEntryService) {
        this.inspireOmLineRepository = inspireOmLineRepository;
        this.inspireOmLineService = inspireOmLineService;
        this.inspireOmProjectRepository = inspireOmProjectRepository;
        this.inspireOmLinesRepository = inspireOmLinesRepository;
        this.logEntryRepository = logEntryRepository;
        this.logEntryService = logEntryService;
    }

    @Transactional
    public void updateInspireOmProject(Long spirProjectId, Long logId, String temp1, String temp2, String temp3, String temp4, String temp5) {
        try {
            inspireOmLineRepository.updateProjectStatus(spirProjectId, 'E');
            inspireOmLineRepository.updateProcessed(spirProjectId, 'E');
            logEntryService.updateLogEntry(logId);
            inspireOmLineService.addLogDetails(logId, spirProjectId, temp1, temp2, temp3, temp4, temp5, entityManager.createNativeQuery("SELECT @@ROWCOUNT").getSingleResult());
            entityManager.flush();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            logEntryService.addLogDetails(logId, "Error updating Inspire OM project and lines");
        }
    }

    @Transactional
    public void updateOmProject(Long spirProjectId) {
        try {
            inspireOmProjectRepository.updateOmProject(spirProjectId);
            inspireOmLinesRepository.updateOmLines(spirProjectId);
            logEntryService.updateLogEntry(logId, "Update OM project and lines");
            entityManager.flush();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            logEntryService.addLogDetails(logId, "Error updating OM project and lines");
        }
    }
}