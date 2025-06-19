package com.inspire.service.impl;

import com.inspire.repository.*;
import com.inspire.service.ProjectResetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectResetServiceImpl implements ProjectResetService {
    private final InspirePartsGroupedRecalcRepository partsGroupedRecalcRepository;
    private final InspireTempPosActivityMappingRepository tempPosActivityMappingRepository;
    private final InspireSpirRefreshRepository spirRefreshRepository;
    private final InspireProjectRepository projectRepository;
    private final InspireOmProjectRepository omProjectRepository;
    private final InspireOmLineRepository omLineRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int resetProject(String projectId) {
        int totalAffectedRows = 0;
        int affectedRows;

        try {
            // Create main log entry
            log.info("Starting technical reset for project {}", projectId);

            // 1. Mark records in inspire_parts_grouped_recalc as processed
            affectedRows = partsGroupedRecalcRepository.updateProcessedFlagByProjectId(projectId);
            totalAffectedRows += affectedRows;
            log.info("Updated {} records in inspire_parts_grouped_recalc for project {}", affectedRows, projectId);

            // 2. Remove temporary position-to-activity mappings
            affectedRows = tempPosActivityMappingRepository.deleteByProjectId(projectId);
            totalAffectedRows += affectedRows;
            log.info("Deleted {} records from inspire_temp_pos_activity_mapping for project {}", affectedRows, projectId);

            // 3. Update SPIR refresh data
            affectedRows = spirRefreshRepository.updateProcessedFlagByProjectId(projectId);
            totalAffectedRows += affectedRows;
            log.info("Updated {} records in inspire_spir_refresh for project {}", affectedRows, projectId);

            // 4. Update project status to COMPLETED
            String username = "SYSTEM"; // This could be obtained from security context in a real application
            affectedRows = projectRepository.updateProjectStatus(projectId, "COMPLETED", LocalDateTime.now(), username);
            totalAffectedRows += affectedRows;
            log.info("Updated project status to COMPLETED for project {}", projectId);

            // 5. Mark pending OM project records with error
            affectedRows = omProjectRepository.updatePendingProjectsWithError(projectId, "Forced om callback");
            totalAffectedRows += affectedRows;
            log.info("Updated {} pending OM project records with error for project {}", affectedRows, projectId);

            // 6. Mark pending OM line records with error
            affectedRows = omLineRepository.updatePendingLinesWithError(projectId, "Forced om callback");
            totalAffectedRows += affectedRows;
            log.info("Updated {} pending OM line records with error for project {}", affectedRows, projectId);

            // Log successful completion
            log.info("Technical reset completed successfully for project {}. Total affected rows: {}", projectId, totalAffectedRows);
            
            return 0; // Success
        } catch (Exception e) {
            // Log error and return failure code
            log.error("Technical reset failed for project {}: {}", projectId, e.getMessage(), e);
            return -1; // Failure
        }
    }
}