package com.inspire.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.inspire.repository.*;
import com.inspire.service.ProjectTechnicalResetService;

@Service
public class ProjectTechnicalResetServiceImpl implements ProjectTechnicalResetService {
    private static final Logger logger = LoggerFactory.getLogger(ProjectTechnicalResetServiceImpl.class);

    private final PositionActivityMappingRepository positionActivityMappingRepository;
    private final InspirePartsGroupedRecalcRepository inspirePartsGroupedRecalcRepository;
    private final InspireSpirRefreshDataRepository inspireSpirRefreshDataRepository;
    private final InspireProjectRepository inspireProjectRepository;
    private final InspireOmProjectRepository inspireOmProjectRepository;
    private final InspireOmLinesRepository inspireOmLinesRepository;

    @Autowired
    public ProjectTechnicalResetServiceImpl(
            PositionActivityMappingRepository positionActivityMappingRepository,
            InspirePartsGroupedRecalcRepository inspirePartsGroupedRecalcRepository,
            InspireSpirRefreshDataRepository inspireSpirRefreshDataRepository,
            InspireProjectRepository inspireProjectRepository,
            InspireOmProjectRepository inspireOmProjectRepository,
            InspireOmLinesRepository inspireOmLinesRepository) {
        this.positionActivityMappingRepository = positionActivityMappingRepository;
        this.inspirePartsGroupedRecalcRepository = inspirePartsGroupedRecalcRepository;
        this.inspireSpirRefreshDataRepository = inspireSpirRefreshDataRepository;
        this.inspireProjectRepository = inspireProjectRepository;
        this.inspireOmProjectRepository = inspireOmProjectRepository;
        this.inspireOmLinesRepository = inspireOmLinesRepository;
    }

    @Override
    @Transactional
    public int performTechnicalReset(Integer projectId) {
        logger.info("Technical reset starting for project ID: {}", projectId);
        
        try {
            // 1. Remove temporary position-to-activity mappings
            int tempPositionsRemoved = positionActivityMappingRepository.deleteByProjectId(projectId);
            logger.debug("Temporary positions removed: {}", tempPositionsRemoved);
            
            // 2. Mark records in parts grouped recalc as processed
            int partsRecalcUpdated = inspirePartsGroupedRecalcRepository.updateProcessedFlagByProjectId(projectId, 'Y');
            logger.debug("Parts recalc records updated: {}", partsRecalcUpdated);
            
            // 3. Update SPIR refresh data to processed status
            int spirRefreshUpdated = inspireSpirRefreshDataRepository.updateProcessedFlagByProjectId(projectId, 'Y');
            logger.debug("SPIR refresh records updated: {}", spirRefreshUpdated);
            
            // 4. Set project status to COMPLETED
            int projectStatusUpdated = inspireProjectRepository.updateProjectStatus(projectId, "COMPLETED");
            logger.debug("Project status updated: {}", projectStatusUpdated);
            
            // 5. Force error states on in-process records in OM project
            int omProjectUpdated = inspireOmProjectRepository.updateStatusForPendingRecords(projectId, 'P', 'E');
            logger.debug("OM project records updated: {}", omProjectUpdated);
            
            // 6. Set error flags on related OM lines records
            int omLinesUpdated = inspireOmLinesRepository.updateStatusForPendingOrUnprocessedRecords(projectId, 'P', 'E', 'Y');
            logger.debug("OM lines records updated: {}", omLinesUpdated);
            
            logger.info("Technical reset completed successfully for project ID: {}", projectId);
            logger.info("Reset summary - Temporary positions: {}, Parts recalc: {}, SPIR refresh: {}, Project status: {}, OM project: {}, OM lines: {}",
                    tempPositionsRemoved, partsRecalcUpdated, spirRefreshUpdated, projectStatusUpdated, omProjectUpdated, omLinesUpdated);
            
            return 0; // Success
        } catch (Exception e) {
            logger.error("Error during technical reset for project ID: {}", projectId, e);
            // Transaction will be rolled back automatically due to @Transactional annotation
            
            // Update project status to reflect error - this will be in a new transaction
            try {
                updateProjectStatusAfterError(projectId);
            } catch (Exception ex) {
                logger.error("Failed to update project status after error for project ID: {}", projectId, ex);
            }
            
            return -1; // Failure
        }
    }
    
    @Transactional(noRollbackFor = Exception.class)
    private void updateProjectStatusAfterError(Integer projectId) {
        inspireProjectRepository.updateProjectStatus(projectId, "ERROR");
        logger.info("Project status updated to ERROR for project ID: {}", projectId);
    }
}