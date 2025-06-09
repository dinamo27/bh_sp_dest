package com.example.project.service;

import com.example.project.entity.InspireOmLines;
import com.example.project.entity.InspireLog;
import com.example.project.model.InspireLogUpdateRequest;
import com.example.project.model.InspireOmLinesUpdateRequest;
import com.example.project.repository.InspireOmLinesRepository;
import com.example.project.repository.InspireLogRepository;
import com.example.project.service.InspireLogService;
import com.example.project.service.InspireProjectProcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InspireOmLinesService {

    private final InspireOmLinesRepository inspireOmLinesRepository;
    private final InspireLogRepository inspireLogRepository;
    private final InspireLogService inspireLogService;
    private final InspireProjectProcService inspireProjectProcService;

    @Autowired
    public InspireOmLinesService(InspireOmLinesRepository inspireOmLinesRepository, InspireLogRepository inspireLogRepository, InspireLogService inspireLogService, InspireProjectProcService inspireProjectProcService) {
        this.inspireOmLinesRepository = inspireOmLinesRepository;
        this.inspireLogRepository = inspireLogRepository;
        this.inspireLogService = inspireLogService;
        this.inspireProjectProcService = inspireProjectProcService;
    }

    @Transactional
    public void updateInspireOmLines(Long spirProjectId, Long logId, String temp1, String temp2, String temp3, String temp4, String temp5) {
        if (spirProjectId == null) {
            throw new RuntimeException("spirProjectId cannot be null");
        }

        try {
            List<InspireOmLines> inspireOmLinesList = inspireOmLinesRepository.findBySpirProjectIdAndProcessed(spirProjectId, 'P');
            for (InspireOmLines inspireOmLines : inspireOmLinesList) {
                inspireOmLines.setProcessed('E');
            }
            inspireOmLinesRepository.saveAll(inspireOmLinesList);
            addLogDetails(logId, spirProjectId, temp1, temp2, temp3, temp4, temp5);
        } catch (Exception e) {
            inspireLogService.updateLogEntry(logId, e.getMessage(), "ERROR");
            inspireProjectProcService.updateInspireProjectProc(spirProjectId);
            throw e;
        }
    }

    public void addLogDetails(Long logId, Long spirProjectId, String temp1, String temp2, String temp3, String temp4, String temp5) {
        InspireLog inspireLog = new InspireLog();
        inspireLog.setLogId(logId);
        inspireLog.setSpirProjectId(spirProjectId);
        inspireLog.setTemp1(temp1);
        inspireLog.setTemp2(temp2);
        inspireLog.setTemp3(temp3);
        inspireLog.setTemp4(temp4);
        inspireLog.setTemp5(temp5);
        inspireLogRepository.save(inspireLog);
    }

    public void updateLogEntry(Long logId, String message, String type) {
        InspireLog inspireLog = inspireLogRepository.findById(logId).orElseThrow();
        inspireLog.setMessage(message);
        inspireLog.setType(type);
        inspireLogRepository.save(inspireLog);
    }

    public void updateInspireProjectProc(Long spirProjectId) {
        inspireProjectProcService.updateInspireProjectProc(spirProjectId);
    }
}