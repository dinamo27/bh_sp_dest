package com.example.project.service;

import com.example.project.entity.InspireOmLine;
import com.example.project.repository.InspireOmLineRepository;
import com.example.project.service.InspireLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class InspireOmLineService {

    private final InspireOmLineRepository inspireOmLineRepository;
    private final InspireLogService inspireLogService;

    @Autowired
    public InspireOmLineService(InspireOmLineRepository inspireOmLineRepository, InspireLogService inspireLogService) {
        this.inspireOmLineRepository = inspireOmLineRepository;
        this.inspireLogService = inspireLogService;
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void updateInspireOmLines(Long spirProjectId, Long logId, String temp1, String temp2, String temp3, String temp4, String temp5) {
        try {
            inspireOmLineRepository.updateProcessed(spirProjectId);
            inspireLogService.logUpdate(logId, "COMPLETED", "MESSAGE");
            entityManager.getTransaction().commit();
            inspireLogService.addLogDetails(logId, spirProjectId, temp1, temp2, temp3, temp4, temp5);
            int successFlag = 0;
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            int successFlag = -1;
            String returnMessage = e.getMessage();
            inspireLogService.logError(logId, returnMessage);
            inspireLogService.updateInspireProjectProc(spirProjectId);
        }
    }

    @Transactional
    public void updateProcessed(Long spirProjectId) {
        inspireOmLineRepository.updateProcessed(spirProjectId);
    }

    @Transactional
    public void deleteInspireOmLines(Long spirProjectId) {
        inspireOmLineRepository.deleteBySpirProjectId(spirProjectId);
    }
}