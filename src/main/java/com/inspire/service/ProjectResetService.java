package com.inspire.service;

import com.inspire.model.*;
import com.inspire.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectResetService {
    private static final Logger logger = LoggerFactory.getLogger(ProjectResetService.class);
    
    private final ProjectRepository projectRepository;
    private final PartsGroupedRecalcRepository partsGroupedRecalcRepository;
    private final SpirRefreshDataRepository spirRefreshDataRepository;
    private final PositionToActivityMappingRepository positionToActivityMappingRepository;
    private final OperationalManagementProjectRepository operationalManagementProjectRepository;
    private final OperationalManagementLinesRepository operationalManagementLinesRepository;

    @Autowired
    public ProjectResetService(
            ProjectRepository projectRepository,
            PartsGroupedRecalcRepository partsGroupedRecalcRepository,
            SpirRefreshDataRepository spirRefreshDataRepository,
            PositionToActivityMappingRepository positionToActivityMappingRepository,
            OperationalManagementProjectRepository operationalManagementProjectRepository,
            OperationalManagementLinesRepository operationalManagementLinesRepository) {
        this.projectRepository = projectRepository;
        this.partsGroupedRecalcRepository = partsGroupedRecalcRepository;
        this.spirRefreshDataRepository = spirRefreshDataRepository;
        this.positionToActivityMappingRepository = positionToActivityMappingRepository;
        this.operationalManagementProjectRepository = operationalManagementProjectRepository;
        this.operationalManagementLinesRepository = operationalManagementLinesRepository;
    }
    
    @Transactional(rollbackFor = Exception.class)
    public int resetProject(Integer projectId) {
        int status = 0;
        String errorMessage = "";
        
        logger.info("Starting technical reset for project {}", projectId);
        
        try {
            int projectRowsAffected = projectRepository.updateProjectStatusToCompleted(projectId);
            logger.info("Project status update: {} rows affected. Set status to COMPLETED", projectRowsAffected);
            
            int partsRowsAffected = partsGroupedRecalcRepository.markAsProcessedByProjectId(projectId);
            logger.info("Parts recalc update: {} rows affected. Marked as processed", partsRowsAffected);
            
            int spirRowsAffected = spirRefreshDataRepository.markAsProcessedByProjectId(projectId);
            logger.info("SPIR data update: {} rows affected. Marked as processed", spirRowsAffected);
            
            int mappingRowsAffected = positionToActivityMappingRepository.deleteByProjectId(projectId);
            logger.info("Position mapping cleanup: {} rows affected. Removed temporary mappings", mappingRowsAffected);
            
            int omProjectRowsAffected = operationalManagementProjectRepository.updatePendingToErrorByProjectId(projectId, "Forced om callback");
            logger.info("OM project update: {} rows affected. Set error status on pending records", omProjectRowsAffected);
            
            int omLinesRowsAffected = operationalManagementLinesRepository.updatePendingToErrorByProjectId(projectId, "Forced om callback");
            logger.info("OM lines update: {} rows affected. Set error status on pending records", omLinesRowsAffected);
            
            logger.info("Technical reset completed successfully for project {}", projectId);
            
        } catch (Exception e) {
            status = -1;
            errorMessage = e.getMessage();
            
            logger.error("Error during technical reset for project {}: {}", projectId, errorMessage, e);
            
            throw e;
        }
        
        return status;
    }
}