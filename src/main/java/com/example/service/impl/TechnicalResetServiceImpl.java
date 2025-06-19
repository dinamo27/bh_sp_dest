package com.example.service.impl;

import com.example.service.TechnicalResetService;
import com.example.service.LoggingService;
import com.example.repository.PartsGroupedRecalcRepository;
import com.example.repository.TempPosActivityMappingRepository;
import com.example.repository.SpirRefreshRepository;
import com.example.repository.ProjectRepository;
import com.example.repository.OmProjectRepository;
import com.example.repository.OmLinesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TechnicalResetServiceImpl implements TechnicalResetService {

    private final PartsGroupedRecalcRepository partsGroupedRecalcRepository;
    private final TempPosActivityMappingRepository tempPosActivityMappingRepository;
    private final SpirRefreshRepository spirRefreshRepository;
    private final ProjectRepository projectRepository;
    private final OmProjectRepository omProjectRepository;
    private final OmLinesRepository omLinesRepository;
    private final LoggingService loggingService;

    @Autowired
    public TechnicalResetServiceImpl(
            PartsGroupedRecalcRepository partsGroupedRecalcRepository,
            TempPosActivityMappingRepository tempPosActivityMappingRepository,
            SpirRefreshRepository spirRefreshRepository,
            ProjectRepository projectRepository,
            OmProjectRepository omProjectRepository,
            OmLinesRepository omLinesRepository,
            LoggingService loggingService) {
        this.partsGroupedRecalcRepository = partsGroupedRecalcRepository;
        this.tempPosActivityMappingRepository = tempPosActivityMappingRepository;
        this.spirRefreshRepository = spirRefreshRepository;
        this.projectRepository = projectRepository;
        this.omProjectRepository = omProjectRepository;
        this.omLinesRepository = omLinesRepository;
        this.loggingService = loggingService;
    }

    @Override
    @Transactional
    public int resetProject(Long projectId) {
        int totalAffectedRows = 0;
        boolean success = false;
        Long logId = loggingService.createMainLogEntry("Technical reset for project: " + projectId);

        try {
            // Step 1: Update records in inspire_parts_grouped_recalc
            int partsGroupedRecalcRows = partsGroupedRecalcRepository.resetByProjectId(projectId);
            totalAffectedRows += partsGroupedRecalcRows;
            loggingService.addLogDetail(logId, "Updated inspire_parts_grouped_recalc", partsGroupedRecalcRows + " rows affected");

            // Step 2: Delete records from inspire_temp_pos_activity_mapping
            int tempPosActivityMappingRows = tempPosActivityMappingRepository.deleteByProjectId(projectId);
            totalAffectedRows += tempPosActivityMappingRows;
            loggingService.addLogDetail(logId, "Deleted from inspire_temp_pos_activity_mapping", tempPosActivityMappingRows + " rows affected");

            // Step 3: Update records in inspire_spir_refresh
            int spirRefreshRows = spirRefreshRepository.resetByProjectId(projectId);
            totalAffectedRows += spirRefreshRows;
            loggingService.addLogDetail(logId, "Updated inspire_spir_refresh", spirRefreshRows + " rows affected");

            // Step 4: Update the project status in inspire_project
            int projectRows = projectRepository.resetStatusByProjectId(projectId);
            totalAffectedRows += projectRows;
            loggingService.addLogDetail(logId, "Updated inspire_project status", projectRows + " rows affected");

            // Step 5: Update pending records in inspire_om_project
            int omProjectRows = omProjectRepository.resetPendingByProjectId(projectId);
            totalAffectedRows += omProjectRows;
            loggingService.addLogDetail(logId, "Updated inspire_om_project", omProjectRows + " rows affected");

            // Step 6: Update pending records in inspire_om_lines
            int omLinesRows = omLinesRepository.resetPendingByProjectId(projectId);
            totalAffectedRows += omLinesRows;
            loggingService.addLogDetail(logId, "Updated inspire_om_lines", omLinesRows + " rows affected");

            success = true;
            loggingService.updateMainLogEntry(logId, "Technical reset completed successfully", "Total affected rows: " + totalAffectedRows, true);
            return 0;
        } catch (Exception e) {
            loggingService.updateMainLogEntry(logId, "Technical reset failed: " + e.getMessage(), "Error during reset operation", false);
            return -1;
        }
    }
}