package com.inspire.service.impl;

import com.inspire.service.SpirProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SpirProjectServiceImpl implements SpirProjectService {

    private static final Logger logger = LoggerFactory.getLogger(SpirProjectServiceImpl.class);
    
    private static final int SUCCESS_CODE = 0;
    private static final int ERROR_CODE = -1;
    private static final String COMPLETED_STATUS = "COMPLETED";
    private static final String ERROR_FLAG = "E";
    private static final String PROCESSED_FLAG = "Y";
    private static final String PENDING_FLAG = "P";
    
    @Autowired
    private PositionToActivityRepository positionToActivityRepository;
    
    @Autowired
    private InspirePartsGroupedRecalcRepository inspirePartsGroupedRecalcRepository;
    
    @Autowired
    private InspireSpirRefreshDataRepository inspireSpirRefreshDataRepository;
    
    @Autowired
    private InspireProjectRepository inspireProjectRepository;
    
    @Autowired
    private InspireOmProjectRepository inspireOmProjectRepository;
    
    @Autowired
    private InspireOmLinesRepository inspireOmLinesRepository;

    @Override
    @Transactional
    public int resetAndFinalizeProject(Long projectId) {
        logger.info("Starting resetAndFinalizeProject operation for projectId: {}", projectId);
        
        int affectedRows = 0;
        
        try {
            // Step 1: Delete position-to-activity mappings
            int deletedMappings = positionToActivityRepository.deleteByProjectId(projectId);
            logger.info("Deleted {} position-to-activity mappings for projectId: {}", deletedMappings, projectId);
            affectedRows += deletedMappings;
            
            // Step 2: Update inspire_parts_grouped_recalc
            int updatedPartsGrouped = inspirePartsGroupedRecalcRepository.updateProcessedFlagByProjectId(projectId, PROCESSED_FLAG);
            logger.info("Updated {} records in inspire_parts_grouped_recalc for projectId: {}", updatedPartsGrouped, projectId);
            affectedRows += updatedPartsGrouped;
            
            // Step 3: Update inspire_spir_refresh_data
            int updatedRefreshData = inspireSpirRefreshDataRepository.updateProcessedFlagByProjectId(projectId, PROCESSED_FLAG);
            logger.info("Updated {} records in inspire_spir_refresh_data for projectId: {}", updatedRefreshData, projectId);
            affectedRows += updatedRefreshData;
            
            // Step 4: Update project status
            int updatedProjectStatus = inspireProjectRepository.updateProjectStatus(projectId, COMPLETED_STATUS);
            logger.info("Updated project status to {} for projectId: {}, affected rows: {}", COMPLETED_STATUS, projectId, updatedProjectStatus);
            affectedRows += updatedProjectStatus;
            
            // Step 5: Update inspire_om_project
            int updatedOmProject = inspireOmProjectRepository.updateErrorFlagForPendingByProjectId(projectId, ERROR_FLAG, PENDING_FLAG);
            logger.info("Updated {} records in inspire_om_project for projectId: {}", updatedOmProject, projectId);
            affectedRows += updatedOmProject;
            
            // Step 6: Update inspire_om_lines
            int updatedOmLines = inspireOmLinesRepository.updateErrorFlagForPendingByProjectId(projectId, ERROR_FLAG, PENDING_FLAG);
            logger.info("Updated {} records in inspire_om_lines for projectId: {}", updatedOmLines, projectId);
            affectedRows += updatedOmLines;
            
            logger.info("Successfully completed resetAndFinalizeProject for projectId: {}, total affected rows: {}", projectId, affectedRows);
            return SUCCESS_CODE;
            
        } catch (Exception e) {
            logger.error("Error in resetAndFinalizeProject for projectId: {}", projectId, e);
            return ERROR_CODE;
        }
    }
}