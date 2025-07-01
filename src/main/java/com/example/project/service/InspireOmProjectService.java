package com.example.project.service;

import com.example.project.entity.InspireOmProject;
import com.example.project.repository.InspireOmProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

@Service
public class InspireOmProjectService {

    private final InspireOmProjectRepository inspireOmProjectRepository;

    @Autowired
    public InspireOmProjectService(InspireOmProjectRepository inspireOmProjectRepository) {
        this.inspireOmProjectRepository = inspireOmProjectRepository;
    }

    @Transactional
    public int updateInspireOmProject(Long spirProj, Long logId, String temp1, String temp2, String temp3, String temp4) {
        try {
            // Validate input data
            if (spirProj == null || logId == null) {
                throw new SQLException("Invalid input data");
            }

            // Filter inspire_om_project table by spir_project_id and process_flag
            int rowCount = inspireOmProjectRepository.updateProcessFlagAndErrorMessage(spirProj, "P", null);

            // Filter inspire_om_lines table by spir_project_id and processed
            inspireOmProjectRepository.updateProcessedStatus(spirProj, "P", null);

            // Log update operation and its outcome
            inspireOmProjectRepository.updateLogEntry(logId, "COMPLETED", "MESSAGE");

            // Add log details
            inspireOmProjectRepository.addLogDetails(logId, spirProj, temp1, temp2, temp3, temp4, rowCount);

            return 0;
        } catch (SQLException e) {
            // Rollback transaction
            inspireOmProjectRepository.rollbackTransaction();

            // Update log entry with error message and 'ERROR' status
            inspireOmProjectRepository.updateLogEntry(logId, "ERROR", e.getMessage());

            // Update inspire_project_proc with spirProj
            inspireOmProjectRepository.updateInspireProjectProc(spirProj);

            return -1;
        } finally {
            // Close database session
            inspireOmProjectRepository.closeSession();
        }
    }
}