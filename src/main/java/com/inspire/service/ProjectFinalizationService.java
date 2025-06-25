package com.inspire.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inspire.repository.InspireProjectRepository;
import com.inspire.repository.InspirePartsGroupedRecalcRepository;
import com.inspire.repository.InspireSpirRefreshDataRepository;
import com.inspire.repository.InspireOmProjectRepository;
import com.inspire.repository.InspireOmLinesRepository;
import com.inspire.repository.PositionToActivityRepository;

@Service
public class ProjectFinalizationService {
    private static final int SUCCESS_CODE = 0;
    private static final int ERROR_CODE = -1;
    private static final String COMPLETED_STATUS = "COMPLETED";
    private static final String ERROR_FLAG = "E";
    private static final String PROCESSED_FLAG = "Y";
    private static final String PENDING_FLAG = "P";

    private static final Logger logger = LoggerFactory.getLogger(ProjectFinalizationService.class);

    private final InspireProjectRepository projectRepository;
    private final InspirePartsGroupedRecalcRepository partsGroupedRecalcRepository;
    private final InspireSpirRefreshDataRepository spirRefreshDataRepository;
    private final InspireOmProjectRepository omProjectRepository;
    private final InspireOmLinesRepository omLinesRepository;
    private final PositionToActivityRepository positionToActivityRepository;

    public ProjectFinalizationService(InspireProjectRepository projectRepository,
                                     InspirePartsGroupedRecalcRepository partsGroupedRecalcRepository,
                                     InspireSpirRefreshDataRepository spirRefreshDataRepository,
                                     InspireOmProjectRepository omProjectRepository,
                                     InspireOmLinesRepository omLinesRepository,
                                     PositionToActivityRepository positionToActivityRepository) {
        this.projectRepository = projectRepository;
        this.partsGroupedRecalcRepository = partsGroupedRecalcRepository;
        this.spirRefreshDataRepository = spirRefreshDataRepository;
        this.omProjectRepository = omProjectRepository;
        this.omLinesRepository = omLinesRepository;
        this.positionToActivityRepository = positionToActivityRepository;
    }

    @Transactional
    public int resetAndFinalizeSPIRProject(Long projectId) {
        int deletedPosActivityCount = 0;
        int updatedPartsGroupedCount = 0;
        int updatedSpirRefreshCount = 0;
        int updatedOmProjectCount = 0;
        int updatedOmLinesCount = 0;

        logger.info("Starting reset and finalization of SPIR project {}", projectId);

        try {
            deletedPosActivityCount = positionToActivityRepository.deleteByProjectId(projectId);
            logger.info("Deleted {} position-to-activity mappings for project {}", deletedPosActivityCount, projectId);

            updatedPartsGroupedCount = partsGroupedRecalcRepository.updateProcessedFlagByProjectId(projectId, PROCESSED_FLAG);
            logger.info("Updated {} records in inspire_parts_grouped_recalc for project {}", updatedPartsGroupedCount, projectId);

            updatedSpirRefreshCount = spirRefreshDataRepository.updateProcessedFlagByProjectId(projectId, PROCESSED_FLAG);
            logger.info("Updated {} records in inspire_spir_refresh_data for project {}", updatedSpirRefreshCount, projectId);

            int projectUpdateCount = projectRepository.updateProjectStatus(projectId, COMPLETED_STATUS);
            logger.info("Updated project {} status to {}", projectId, COMPLETED_STATUS);

            updatedOmProjectCount = omProjectRepository.updateErrorFlagForPendingByProjectId(projectId, ERROR_FLAG, PENDING_FLAG);
            logger.info("Updated {} records in inspire_om_project for project {}", updatedOmProjectCount, projectId);

            updatedOmLinesCount = omLinesRepository.updateErrorFlagForPendingByProjectId(projectId, ERROR_FLAG, PENDING_FLAG);
            logger.info("Updated {} records in inspire_om_lines for project {}", updatedOmLinesCount, projectId);

            int totalAffectedRows = deletedPosActivityCount + updatedPartsGroupedCount + updatedSpirRefreshCount + 
                                   updatedOmProjectCount + updatedOmLinesCount + projectUpdateCount;
            logger.info("Successfully reset and finalized SPIR project {}. Total affected rows: {}", projectId, totalAffectedRows);

            return SUCCESS_CODE;
        } catch (Exception e) {
            logger.error("Error resetting and finalizing SPIR project {}: {}", projectId, e.getMessage(), e);
            throw e;
        }
    }
}