package com.inspire.service.impl;

import com.inspire.model.Project;
import com.inspire.repository.OmLineRepository;
import com.inspire.repository.OmProjectRepository;
import com.inspire.repository.PartsGroupedRecalcRepository;
import com.inspire.repository.ProjectRepository;
import com.inspire.repository.SpirRefreshDataRepository;
import com.inspire.repository.TempPosToActivityRepository;
import com.inspire.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ProjectServiceImpl implements ProjectService {
    private static final Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);
    private static final String COMPLETED_STATUS = "COMPLETED";
    private static final String FORCED_ERROR_MESSAGE = "Forced om callback";
    
    private final PartsGroupedRecalcRepository partsGroupedRecalcRepository;
    private final SpirRefreshDataRepository spirRefreshDataRepository;
    private final TempPosToActivityRepository tempPosToActivityRepository;
    private final ProjectRepository projectRepository;
    private final OmProjectRepository omProjectRepository;
    private final OmLineRepository omLineRepository;
    
    public ProjectServiceImpl(
            PartsGroupedRecalcRepository partsGroupedRecalcRepository,
            SpirRefreshDataRepository spirRefreshDataRepository,
            TempPosToActivityRepository tempPosToActivityRepository,
            ProjectRepository projectRepository,
            OmProjectRepository omProjectRepository,
            OmLineRepository omLineRepository) {
        this.partsGroupedRecalcRepository = partsGroupedRecalcRepository;
        this.spirRefreshDataRepository = spirRefreshDataRepository;
        this.tempPosToActivityRepository = tempPosToActivityRepository;
        this.projectRepository = projectRepository;
        this.omProjectRepository = omProjectRepository;
        this.omLineRepository = omLineRepository;
    }
    
    @Override
    @Transactional
    public boolean technicalResetProject(Long projectId) {
        int affectedPartsRecords = 0;
        int affectedSpirRecords = 0;
        int affectedPosActivityRecords = 0;
        int affectedOmProjectRecords = 0;
        int affectedOmLinesRecords = 0;
        
        logger.info("Starting technical reset for project ID: {}", projectId);
        
        try {
            Optional<Project> projectOpt = projectRepository.findById(projectId);
            if (projectOpt.isEmpty()) {
                logger.error("Project with ID {} not found", projectId);
                throw new IllegalArgumentException("Project not found with ID: " + projectId);
            }
            
            affectedPartsRecords = partsGroupedRecalcRepository.markProcessedByProjectId(projectId);
            logger.info("Marked {} parts grouped recalc records as processed for project ID: {}", 
                    affectedPartsRecords, projectId);
            
            affectedSpirRecords = spirRefreshDataRepository.markProcessedByProjectId(projectId);
            logger.info("Marked {} SPIR refresh data records as processed for project ID: {}", 
                    affectedSpirRecords, projectId);
            
            affectedPosActivityRecords = tempPosToActivityRepository.deleteByProjectId(projectId);
            logger.info("Deleted {} temporary position-to-activity records for project ID: {}", 
                    affectedPosActivityRecords, projectId);
            
            affectedOmProjectRecords = omProjectRepository.forceErrorStateByProjectId(projectId, FORCED_ERROR_MESSAGE);
            logger.info("Updated {} OM project records with error state for project ID: {}", 
                    affectedOmProjectRecords, projectId);
            
            affectedOmLinesRecords = omLineRepository.forceErrorStateByProjectId(projectId, FORCED_ERROR_MESSAGE);
            logger.info("Updated {} OM lines records with error state for project ID: {}", 
                    affectedOmLinesRecords, projectId);
            
            Project project = projectOpt.get();
            project.setStatus(COMPLETED_STATUS);
            project.setRecalcRequired(false);
            project.setRefreshRequired(false);
            projectRepository.save(project);
            logger.info("Updated project status to COMPLETED for project ID: {}", projectId);
            
            logger.info("Technical reset completed successfully for project ID: {}. Metrics: " +
                    "Parts records processed: {}, SPIR records processed: {}, " +
                    "Position-activity records deleted: {}, OM project records updated: {}, " +
                    "OM lines records updated: {}",
                    projectId, affectedPartsRecords, affectedSpirRecords, 
                    affectedPosActivityRecords, affectedOmProjectRecords, affectedOmLinesRecords);
            
            return true;
            
        } catch (Exception e) {
            logger.error("Technical reset failed for project ID: {}: {}", projectId, e.getMessage(), e);
            return false;
        }
    }
}