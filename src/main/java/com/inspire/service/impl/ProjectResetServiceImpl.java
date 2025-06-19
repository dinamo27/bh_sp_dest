package com.inspire.service.impl;

import com.inspire.model.*;
import com.inspire.repository.*;
import com.inspire.service.ProjectResetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ProjectResetServiceImpl implements ProjectResetService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProjectResetServiceImpl.class);
    
    private final InspirePartsGroupedRecalcRepository partsGroupedRecalcRepository;
    private final InspireTempPosActivityMappingRepository tempPosActivityMappingRepository;
    private final InspireSpirRefreshRepository spirRefreshRepository;
    private final InspireProjectRepository projectRepository;
    private final InspireOmProjectRepository omProjectRepository;
    private final InspireOmLinesRepository omLinesRepository;
    
    @Autowired
    public ProjectResetServiceImpl(
            InspirePartsGroupedRecalcRepository partsGroupedRecalcRepository,
            InspireTempPosActivityMappingRepository tempPosActivityMappingRepository,
            InspireSpirRefreshRepository spirRefreshRepository,
            InspireProjectRepository projectRepository,
            InspireOmProjectRepository omProjectRepository,
            InspireOmLinesRepository omLinesRepository) {
        this.partsGroupedRecalcRepository = partsGroupedRecalcRepository;
        this.tempPosActivityMappingRepository = tempPosActivityMappingRepository;
        this.spirRefreshRepository = spirRefreshRepository;
        this.projectRepository = projectRepository;
        this.omProjectRepository = omProjectRepository;
        this.omLinesRepository = omLinesRepository;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int resetProject(String projectId) {
        int totalAffectedRows = 0;
        String logId = UUID.randomUUID().toString();
        
        logger.info("[{}] Starting technical reset for project {}", logId, projectId);
        
        try {
            // 1. Mark records in inspire_parts_grouped_recalc as processed
            int affectedRows = partsGroupedRecalcRepository.markAsProcessed(projectId);
            totalAffectedRows += affectedRows;
            logger.info("[{}] Updated inspire_parts_grouped_recalc: {} rows affected", logId, affectedRows);
            
            // 2. Remove temporary position-to-activity mappings
            affectedRows = tempPosActivityMappingRepository.deleteByProjectId(projectId);
            totalAffectedRows += affectedRows;
            logger.info("[{}] Deleted from inspire_temp_pos_activity_mapping: {} rows affected", logId, affectedRows);
            
            // 3. Update SPIR refresh data
            affectedRows = spirRefreshRepository.markAsProcessed(projectId);
            totalAffectedRows += affectedRows;
            logger.info("[{}] Updated inspire_spir_refresh: {} rows affected", logId, affectedRows);
            
            // 4. Update project status to COMPLETED
            affectedRows = projectRepository.completeProject(projectId, LocalDateTime.now(), "SYSTEM");
            totalAffectedRows += affectedRows;
            logger.info("[{}] Updated inspire_project status to COMPLETED: {} rows affected", logId, affectedRows);
            
            // 5. Mark pending OM project records with error
            affectedRows = omProjectRepository.markPendingWithError(projectId, "Forced om callback");
            totalAffectedRows += affectedRows;
            logger.info("[{}] Updated inspire_om_project: {} rows affected", logId, affectedRows);
            
            // 6. Mark pending OM line records with error
            affectedRows = omLinesRepository.markPendingWithError(projectId, "Forced om callback");
            totalAffectedRows += affectedRows;
            logger.info("[{}] Updated inspire_om_lines: {} rows affected", logId, affectedRows);
            
            logger.info("[{}] Technical reset completed successfully. Total rows affected: {}", logId, totalAffectedRows);
            
            return 0; // Success
            
        } catch (Exception e) {
            logger.error("[{}] Technical reset failed: {}", logId, e.getMessage(), e);
            throw e; // Re-throw to trigger transaction rollback
        }
    }
}