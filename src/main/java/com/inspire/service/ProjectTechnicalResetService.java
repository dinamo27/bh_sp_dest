package com.inspire.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.inspire.repository.*;

@Service
public class ProjectTechnicalResetService {
    private static final Logger logger = LoggerFactory.getLogger(ProjectTechnicalResetService.class);

    @Autowired
    private InspireProjectRepository inspireProjectRepository;

    @Autowired
    private InspireOmProjectRepository inspireOmProjectRepository;

    @Autowired
    private InspireOmLinesRepository inspireOmLinesRepository;

    @Autowired
    private InspirePartsGroupedRecalcRepository inspirePartsGroupedRecalcRepository;

    @Autowired
    private InspireSpirRefreshDataRepository inspireSpirRefreshDataRepository;

    @Autowired
    private PositionActivityMappingRepository positionActivityMappingRepository;

    @Transactional
    public int technicalResetProject(Integer projectId) {
        int successFlag = 0;
        int rowsAffectedTempPositions = 0;
        int rowsAffectedPartsRecalc = 0;
        int rowsAffectedSpirRefresh = 0;
        int rowsAffectedProjectStatus = 0;
        int rowsAffectedOmProject = 0;
        int rowsAffectedOmLines = 0;

        logger.info("Technical reset starting for project ID: {}", projectId);

        try {
            // 1. Remove temporary position-to-activity mappings
            rowsAffectedTempPositions = positionActivityMappingRepository.deleteAllByProjectId(projectId);
            logger.debug("Removed {} temporary position-to-activity mappings", rowsAffectedTempPositions);

            // 2. Mark records in parts grouped recalc as processed
            rowsAffectedPartsRecalc = inspirePartsGroupedRecalcRepository.markAllAsProcessed(projectId);
            logger.debug("Updated {} parts grouped recalc records", rowsAffectedPartsRecalc);

            // 3. Update SPIR refresh data to processed status
            rowsAffectedSpirRefresh = inspireSpirRefreshDataRepository.markAllAsProcessed(projectId);
            logger.debug("Updated {} SPIR refresh data records", rowsAffectedSpirRefresh);

            // 4. Set project status to COMPLETED
            rowsAffectedProjectStatus = inspireProjectRepository.updateProjectStatus(projectId, "COMPLETED");
            logger.debug("Updated project status for {} records", rowsAffectedProjectStatus);

            // 5. Force error states on in-process records in OM project
            rowsAffectedOmProject = inspireOmProjectRepository.updatePendingStatusToError(projectId);
            logger.debug("Updated {} OM project records", rowsAffectedOmProject);

            // 6. Set error flags on related OM lines records
            rowsAffectedOmLines = inspireOmLinesRepository.updatePendingOrUnprocessedToError(projectId);
            logger.debug("Updated {} OM lines records", rowsAffectedOmLines);

            // Log detailed results
            logger.info("Technical reset completed successfully for project ID: {}", projectId);
            logger.info("Temporary positions removed: {}", rowsAffectedTempPositions);
            logger.info("Parts recalc records updated: {}", rowsAffectedPartsRecalc);
            logger.info("SPIR refresh records updated: {}", rowsAffectedSpirRefresh);
            logger.info("Project status updated: {}", rowsAffectedProjectStatus);
            logger.info("OM project records updated: {}", rowsAffectedOmProject);
            logger.info("OM lines records updated: {}", rowsAffectedOmLines);

            return successFlag; // Success (0)
        } catch (Exception e) {
            // Log error and set failure return value
            String errorMessage = "Error during technical reset for project ID: " + projectId;
            logger.error(errorMessage, e);
            
            try {
                inspireProjectRepository.updateStatus(projectId, "ERROR");
            } catch (Exception ex) {
                logger.error("Failed to update project status after error: {}", ex.getMessage(), ex);
            }
            
            return -1; // Failure
        }
    }
}