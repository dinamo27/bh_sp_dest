package com.example.service.impl;

import com.example.exception.ProjectNotFoundException;
import com.example.model.Project;
import com.example.repository.OperationalManagementLinesRepository;
import com.example.repository.OperationalManagementProjectRepository;
import com.example.repository.PartsGroupedRecalcRepository;
import com.example.repository.PositionToActivityTempRepository;
import com.example.repository.ProjectRepository;
import com.example.repository.SpirRefreshDataRepository;
import com.example.service.ProjectResetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class ProjectResetServiceImpl implements ProjectResetService {

    private final ProjectRepository projectRepository;
    private final PartsGroupedRecalcRepository partsGroupedRecalcRepository;
    private final SpirRefreshDataRepository spirRefreshDataRepository;
    private final PositionToActivityTempRepository positionToActivityTempRepository;
    private final OperationalManagementProjectRepository operationalManagementProjectRepository;
    private final OperationalManagementLinesRepository operationalManagementLinesRepository;
    
    private static final Logger logger = LoggerFactory.getLogger(ProjectResetServiceImpl.class);

    @Autowired
    public ProjectResetServiceImpl(
            ProjectRepository projectRepository,
            PartsGroupedRecalcRepository partsGroupedRecalcRepository,
            SpirRefreshDataRepository spirRefreshDataRepository,
            PositionToActivityTempRepository positionToActivityTempRepository,
            OperationalManagementProjectRepository operationalManagementProjectRepository,
            OperationalManagementLinesRepository operationalManagementLinesRepository) {
        this.projectRepository = projectRepository;
        this.partsGroupedRecalcRepository = partsGroupedRecalcRepository;
        this.spirRefreshDataRepository = spirRefreshDataRepository;
        this.positionToActivityTempRepository = positionToActivityTempRepository;
        this.operationalManagementProjectRepository = operationalManagementProjectRepository;
        this.operationalManagementLinesRepository = operationalManagementLinesRepository;
    }

    @Override
    public int technicalResetProject(Long projectId) {
        logger.info("Starting technical reset for project with ID: {}", projectId);
        
        int partsGroupedRecalcUpdated = 0;
        int spirRefreshDataUpdated = 0;
        int positionToActivityTempDeleted = 0;
        int projectUpdated = 0;
        int operationalManagementProjectUpdated = 0;
        int operationalManagementLinesUpdated = 0;
        
        try {
            Optional<Project> projectOptional = projectRepository.findById(projectId);
            if (!projectOptional.isPresent()) {
                logger.error("Project with ID {} not found", projectId);
                throw new ProjectNotFoundException("Project with ID " + projectId + " not found");
            }
            
            Project project = projectOptional.get();
            logger.info("Found project: {}", project.toString());
            
            partsGroupedRecalcUpdated = partsGroupedRecalcRepository.updateProcessedStatusByProjectId(projectId, true);
            logger.info("Updated {} records in PartsGroupedRecalc table", partsGroupedRecalcUpdated);
            
            spirRefreshDataUpdated = spirRefreshDataRepository.updateProcessedStatusByProjectId(projectId, true);
            logger.info("Updated {} records in SpirRefreshData table", spirRefreshDataUpdated);
            
            positionToActivityTempDeleted = positionToActivityTempRepository.deleteByProjectId(projectId);
            logger.info("Deleted {} records from PositionToActivityTemp table", positionToActivityTempDeleted);
            
            project.setStatus("COMPLETED");
            projectRepository.save(project);
            projectUpdated = 1;
            logger.info("Updated project status to COMPLETED");
            
            String resetMessage = " - Reset on " + java.time.LocalDateTime.now();
            
            operationalManagementProjectUpdated = operationalManagementProjectRepository.updateStatusAndAppendMessageByProjectId(projectId, "E", resetMessage);
            logger.info("Updated {} records in OperationalManagementProject table", operationalManagementProjectUpdated);
            
            operationalManagementLinesUpdated = operationalManagementLinesRepository.updateStatusAndAppendMessageByProjectId(projectId, "E", resetMessage);
            logger.info("Updated {} records in OperationalManagementLines table", operationalManagementLinesUpdated);
            
            logger.info("Technical reset completed successfully for project ID: {}. Statistics: " +
                    "PartsGroupedRecalc: {}, SpirRefreshData: {}, PositionToActivityTemp: {}, " +
                    "Project: {}, OperationalManagementProject: {}, OperationalManagementLines: {}",
                    projectId, partsGroupedRecalcUpdated, spirRefreshDataUpdated, positionToActivityTempDeleted,
                    projectUpdated, operationalManagementProjectUpdated, operationalManagementLinesUpdated);
            
            return 0; // Success
            
        } catch (ProjectNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error during technical reset for project ID: {}. Error: {}", projectId, e.getMessage(), e);
            return -1; // Failure
        }
    }
}