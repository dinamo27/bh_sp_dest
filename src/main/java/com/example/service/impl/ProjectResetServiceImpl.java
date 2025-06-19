package com.example.service.impl;

import com.example.model.LogDetail;
import com.example.model.LogEntry;
import com.example.repository.InspireOmLineRepository;
import com.example.repository.InspireOmProjectRepository;
import com.example.repository.InspirePartsGroupedRecalcRepository;
import com.example.repository.InspireProjectRepository;
import com.example.repository.InspireSpirRefreshRepository;
import com.example.repository.InspireTempPosActivityMappingRepository;
import com.example.repository.LogDetailRepository;
import com.example.repository.LogEntryRepository;
import com.example.service.ProjectResetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ProjectResetServiceImpl implements ProjectResetService {
    private static final Logger logger = LoggerFactory.getLogger(ProjectResetServiceImpl.class);
    
    private final InspirePartsGroupedRecalcRepository partsGroupedRecalcRepository;
    private final InspireProjectRepository projectRepository;
    private final InspireOmProjectRepository omProjectRepository;
    private final InspireOmLineRepository omLineRepository;
    private final InspireTempPosActivityMappingRepository tempPosActivityMappingRepository;
    private final InspireSpirRefreshRepository spirRefreshRepository;
    private final LogEntryRepository logEntryRepository;
    private final LogDetailRepository logDetailRepository;
    
    @Autowired
    public ProjectResetServiceImpl(
            InspirePartsGroupedRecalcRepository partsGroupedRecalcRepository,
            InspireProjectRepository projectRepository,
            InspireOmProjectRepository omProjectRepository,
            InspireOmLineRepository omLineRepository,
            InspireTempPosActivityMappingRepository tempPosActivityMappingRepository,
            InspireSpirRefreshRepository spirRefreshRepository,
            LogEntryRepository logEntryRepository,
            LogDetailRepository logDetailRepository) {
        this.partsGroupedRecalcRepository = partsGroupedRecalcRepository;
        this.projectRepository = projectRepository;
        this.omProjectRepository = omProjectRepository;
        this.omLineRepository = omLineRepository;
        this.tempPosActivityMappingRepository = tempPosActivityMappingRepository;
        this.spirRefreshRepository = spirRefreshRepository;
        this.logEntryRepository = logEntryRepository;
        this.logDetailRepository = logDetailRepository;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int resetProject(String projectId) {
        int totalAffectedRows = 0;
        
        LogEntry logEntry = createLogEntry(projectId);
        
        try {
            int affectedRows = partsGroupedRecalcRepository.markAsProcessed(projectId);
            totalAffectedRows += affectedRows;
            addLogDetail(logEntry, "Update inspire_parts_grouped_recalc", affectedRows, "Marked records as processed");
            
            affectedRows = tempPosActivityMappingRepository.deleteByProjectId(projectId);
            totalAffectedRows += affectedRows;
            addLogDetail(logEntry, "Delete from inspire_temp_pos_activity_mapping", affectedRows, "Removed temporary mappings");
            
            affectedRows = spirRefreshRepository.markAsProcessed(projectId);
            totalAffectedRows += affectedRows;
            addLogDetail(logEntry, "Update inspire_spir_refresh", affectedRows, "Marked SPIR refresh data as processed");
            
            affectedRows = projectRepository.completeProject(projectId, LocalDateTime.now(), "SYSTEM");
            totalAffectedRows += affectedRows;
            addLogDetail(logEntry, "Update inspire_project", affectedRows, "Set project status to COMPLETED");
            
            affectedRows = omProjectRepository.markPendingWithError(projectId, "Forced om callback");
            totalAffectedRows += affectedRows;
            addLogDetail(logEntry, "Update inspire_om_project", affectedRows, "Marked pending OM project records with error");
            
            affectedRows = omLineRepository.markPendingWithError(projectId, "Forced om callback");
            totalAffectedRows += affectedRows;
            addLogDetail(logEntry, "Update inspire_om_lines", affectedRows, "Marked pending OM line records with error");
            
            updateLogEntry(logEntry, "COMPLETED", totalAffectedRows, "Technical reset completed successfully");
            
            return 0;
        } catch (Exception e) {
            logger.error("Error during technical reset for project {}: {}", projectId, e.getMessage(), e);
            
            updateLogEntry(logEntry, "FAILED", totalAffectedRows, "Technical reset failed: " + e.getMessage());
            
            return -1;
        }
    }
    
    private LogEntry createLogEntry(String projectId) {
        LogEntry logEntry = new LogEntry();
        logEntry.setProjectId(projectId);
        logEntry.setOperation("Technical Reset Project");
        logEntry.setStatus("IN_PROGRESS");
        logEntry.setStartTime(LocalDateTime.now());
        logEntry.setMessage("Starting technical reset for project " + projectId);
        
        logEntryRepository.save(logEntry);
        
        logger.info("Technical Reset Project started for project {}", projectId);
        
        return logEntry;
    }
    
    private void updateLogEntry(LogEntry logEntry, String status, int affectedRows, String message) {
        logEntry.setStatus(status);
        logEntry.setEndTime(LocalDateTime.now());
        logEntry.setAffectedRows(affectedRows);
        logEntry.setMessage(message);
        
        logEntryRepository.save(logEntry);
        
        logger.info("Technical Reset Project {} for project {}: {}", 
                status, logEntry.getProjectId(), message);
    }
    
    private void addLogDetail(LogEntry logEntry, String operation, int affectedRows, String message) {
        LogDetail logDetail = new LogDetail();
        logDetail.setLogId(logEntry.getLogId());
        logDetail.setOperation(operation);
        logDetail.setAffectedRows(affectedRows);
        logDetail.setMessage(message);
        
        logDetailRepository.save(logDetail);
        
        logger.info("Technical Reset Project detail for project {}: {} - {} rows affected - {}", 
                logEntry.getProjectId(), operation, affectedRows, message);
    }
}