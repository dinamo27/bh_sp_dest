package com.inspire.service.impl;

import com.inspire.repository.InspireOmLinesRepository;
import com.inspire.repository.InspireOmProjectRepository;
import com.inspire.repository.InspirePartsGroupedRecalcRepository;
import com.inspire.repository.InspireProjectRepository;
import com.inspire.repository.InspireSpirRefreshRepository;
import com.inspire.repository.InspireTempPosActivityMappingRepository;
import com.inspire.service.TechnicalResetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
public class TechnicalResetServiceImpl implements TechnicalResetService {

    private final InspirePartsGroupedRecalcRepository partsGroupedRecalcRepository;
    private final InspireProjectRepository projectRepository;
    private final InspireOmProjectRepository omProjectRepository;
    private final InspireOmLinesRepository omLinesRepository;
    private final InspireTempPosActivityMappingRepository tempPosActivityMappingRepository;
    private final InspireSpirRefreshRepository spirRefreshRepository;

    @Autowired
    public TechnicalResetServiceImpl(
            InspirePartsGroupedRecalcRepository partsGroupedRecalcRepository,
            InspireProjectRepository projectRepository,
            InspireOmProjectRepository omProjectRepository,
            InspireOmLinesRepository omLinesRepository,
            InspireTempPosActivityMappingRepository tempPosActivityMappingRepository,
            InspireSpirRefreshRepository spirRefreshRepository) {
        this.partsGroupedRecalcRepository = partsGroupedRecalcRepository;
        this.projectRepository = projectRepository;
        this.omProjectRepository = omProjectRepository;
        this.omLinesRepository = omLinesRepository;
        this.tempPosActivityMappingRepository = tempPosActivityMappingRepository;
        this.spirRefreshRepository = spirRefreshRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int resetProject(String projectId) {
        int totalAffectedRows = 0;
        int successFlag = 0;

        log.info("Starting technical reset for project {}", projectId);

        try {
            // 1. Mark records in inspire_parts_grouped_recalc as processed
            int affectedRows = partsGroupedRecalcRepository.updateProcessedFlagByProjectId(projectId, "Y");
            totalAffectedRows += affectedRows;
            log.info("Updated {} records in inspire_parts_grouped_recalc for project {}", affectedRows, projectId);

            // 2. Remove temporary position-to-activity mappings
            affectedRows = tempPosActivityMappingRepository.deleteByProjectId(projectId);
            totalAffectedRows += affectedRows;
            log.info("Deleted {} records from inspire_temp_pos_activity_mapping for project {}", affectedRows, projectId);

            // 3. Update SPIR refresh data
            affectedRows = spirRefreshRepository.updateProcessedFlagByProjectId(projectId, "Y");
            totalAffectedRows += affectedRows;
            log.info("Updated {} records in inspire_spir_refresh for project {}", affectedRows, projectId);

            // 4. Update project status to COMPLETED
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            affectedRows = projectRepository.updateProjectStatusByProjectId(
                    projectId,
                    "COMPLETED",
                    LocalDateTime.now(),
                    username
            );
            totalAffectedRows += affectedRows;
            log.info("Updated {} records in inspire_project for project {}", affectedRows, projectId);

            // 5. Mark pending OM project records with error
            affectedRows = omProjectRepository.updateStatusAndErrorMessageForPendingByProjectId(
                    projectId,
                    "E",
                    "Forced om callback",
                    "P"
            );
            totalAffectedRows += affectedRows;
            log.info("Updated {} records in inspire_om_project for project {}", affectedRows, projectId);

            // 6. Mark pending OM line records with error
            affectedRows = omLinesRepository.updateStatusAndErrorMessageForPendingByProjectId(
                    projectId,
                    "E",
                    "Forced om callback",
                    "P"
            );
            totalAffectedRows += affectedRows;
            log.info("Updated {} records in inspire_om_lines for project {}", affectedRows, projectId);

            log.info("Technical reset completed successfully for project {}. Total affected rows: {}", projectId, totalAffectedRows);
            successFlag = 0;  // Success

        } catch (Exception e) {
            log.error("Technical reset failed for project {}: {}", projectId, e.getMessage(), e);
            successFlag = -1;  // Failure
            throw e;  // Re-throw to trigger transaction rollback
        }

        return successFlag;
    }
}