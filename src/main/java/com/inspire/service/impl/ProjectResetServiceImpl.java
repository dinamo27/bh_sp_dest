package com.inspire.service.impl;

import com.inspire.repository.InspireProjectRepository;
import com.inspire.repository.InspireSpirRefreshDataRepository;
import com.inspire.repository.InspireOmProjectRepository;
import com.inspire.repository.InspireOmLineRepository;
import com.inspire.service.ProjectResetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectResetServiceImpl implements ProjectResetService {
    private static final Logger logger = LoggerFactory.getLogger(ProjectResetServiceImpl.class);

    private static final String COMPLETED_STATUS = "COMPLETED";
    private static final Character ERROR_FLAG = 'E';
    private static final Character PENDING_FLAG = 'P';
    private static final String FORCED_CALLBACK_MESSAGE = "Forced om callback";

    private final InspireProjectRepository projectRepository;
    private final InspireSpirRefreshDataRepository refreshDataRepository;
    private final InspireOmProjectRepository omProjectRepository;
    private final InspireOmLineRepository omLineRepository;

    @Autowired
    public ProjectResetServiceImpl(
            InspireProjectRepository projectRepository,
            InspireSpirRefreshDataRepository refreshDataRepository,
            InspireOmProjectRepository omProjectRepository,
            InspireOmLineRepository omLineRepository) {
        this.projectRepository = projectRepository;
        this.refreshDataRepository = refreshDataRepository;
        this.omProjectRepository = omProjectRepository;
        this.omLineRepository = omLineRepository;
    }

    @Override
    @Transactional
    public boolean resetAndFinalizeProject(Integer projectId) {
        try {
            logger.info("Starting project reset and finalization for project ID: {}", projectId);

            int projectUpdated = projectRepository.updateProjectStatus(projectId, COMPLETED_STATUS);
            logger.info("Updated project status to COMPLETED. Affected rows: {}", projectUpdated);

            int refreshDataUpdated = refreshDataRepository.updateRefreshDataStatus(projectId, String.valueOf(ERROR_FLAG));
            logger.info("Updated refresh data status. Affected rows: {}", refreshDataUpdated);

            int omProjectsUpdated = omProjectRepository.updatePendingOmProjects(
                projectId, ERROR_FLAG, FORCED_CALLBACK_MESSAGE, PENDING_FLAG);
            logger.info("Updated OM projects with error status. Affected rows: {}", omProjectsUpdated);

            int omLinesUpdated = omLineRepository.updatePendingOmLines(
                projectId, ERROR_FLAG, PENDING_FLAG);
            logger.info("Updated OM line items with error status. Affected rows: {}", omLinesUpdated);

            logger.info("Project reset and finalization completed successfully for project ID: {}", projectId);
            return true;
        } catch (Exception e) {
            logger.error("Project reset and finalization failed for project ID: {}", projectId, e);
            return false;
        }
    }
}