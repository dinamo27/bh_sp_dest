package com.inspire.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inspire.repository.PartsGroupedRecalcRepository;
import com.inspire.repository.SpirRefreshDataRepository;
import com.inspire.repository.TempPosToActivityRepository;
import com.inspire.repository.ProjectRepository;
import com.inspire.repository.OmProjectRepository;
import com.inspire.repository.OmLineRepository;
import com.inspire.model.Project;

import java.util.Optional;

@Service
public class TechnicalResetService {

    private final PartsGroupedRecalcRepository partsGroupedRecalcRepository;
    private final SpirRefreshDataRepository spirRefreshDataRepository;
    private final TempPosToActivityRepository tempPosToActivityRepository;
    private final ProjectRepository projectRepository;
    private final OmProjectRepository omProjectRepository;
    private final OmLineRepository omLineRepository;

    private static final Logger logger = LoggerFactory.getLogger(TechnicalResetService.class);

    public TechnicalResetService(
            PartsGroupedRecalcRepository partsGroupedRecalcRepository,
            SpirRefreshDataRepository spirRefreshDataRepository,
            TempPosToActivityRepository tempPosToActivityRepository,
            ProjectRepository projectRepository,
            OmProjectRepository omProjectRepository,
            OmLineRepository omLineRepository
    ) {
        this.partsGroupedRecalcRepository = partsGroupedRecalcRepository;
        this.spirRefreshDataRepository = spirRefreshDataRepository;
        this.tempPosToActivityRepository = tempPosToActivityRepository;
        this.projectRepository = projectRepository;
        this.omProjectRepository = omProjectRepository;
        this.omLineRepository = omLineRepository;
    }

    @Transactional
    public int resetProject(Integer projectId) {
        logger.info("Starting technical reset for project ID: {}", projectId);

        try {
            Optional<Project> projectOpt = projectRepository.findByProjectId(projectId);
            if (projectOpt.isEmpty()) {
                logger.error("Project with ID {} not found", projectId);
                return -1;
            }

            int affectedPartsRecords = 0;
            int affectedSpirRecords = 0;
            int affectedPosActivityRecords = 0;
            int affectedOmProjectRecords = 0;
            int affectedOmLinesRecords = 0;

            affectedPartsRecords = partsGroupedRecalcRepository.markProcessedByProjectId(projectId);
            logger.info("Marked {} parts records as processed for project ID: {}", affectedPartsRecords, projectId);

            affectedSpirRecords = spirRefreshDataRepository.markProcessedByProjectId(projectId);
            logger.info("Marked {} SPIR records as processed for project ID: {}", affectedSpirRecords, projectId);

            affectedPosActivityRecords = tempPosToActivityRepository.deleteByProjectId(projectId);
            logger.info("Deleted {} position-activity records for project ID: {}", affectedPosActivityRecords, projectId);

            affectedOmProjectRecords = omProjectRepository.forceErrorOnUnprocessedRecords(projectId);
            logger.info("Updated {} OM project records for project ID: {}", affectedOmProjectRecords, projectId);

            affectedOmLinesRecords = omLineRepository.forceErrorOnUnprocessedRecords(projectId);
            logger.info("Updated {} OM lines records for project ID: {}", affectedOmLinesRecords, projectId);

            int projectUpdated = projectRepository.updateProjectStatusAndFlags(projectId, "COMPLETED", false, false);
            logger.info("Updated project status for project ID: {}", projectId);

            logger.info("Technical reset completed successfully for project ID: {}", projectId);
            logger.info("Reset metrics - Parts: {}, SPIR: {}, Pos-Activity: {}, OM Projects: {}, OM Lines: {}",
                    affectedPartsRecords, affectedSpirRecords, affectedPosActivityRecords,
                    affectedOmProjectRecords, affectedOmLinesRecords);

            return 0;
        } catch (Exception e) {
            logger.error("Technical reset failed for project ID: {}", projectId, e);
            return -1;
        }
    }
}