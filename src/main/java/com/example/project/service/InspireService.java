package com.example.project.service;

import com.example.project.entity.InspirePositionToActivityTemp;
import com.example.project.entity.InspireProject;
import com.example.project.entity.InspireSpirRefreshData;
import com.example.project.entity.InspireOmProject;
import com.example.project.entity.InspireOmLines;
import com.example.project.repository.InspirePositionToActivityTempRepository;
import com.example.project.repository.InspireSpirRefreshDataRepository;
import com.example.project.repository.InspireProjectRepository;
import com.example.project.repository.InspireOmProjectRepository;
import com.example.project.repository.InspireOmLinesRepository;
import com.example.project.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InspireService {

    private final InspirePositionToActivityTempRepository inspirePositionToActivityTempRepository;
    private final InspireSpirRefreshDataRepository inspireSpirRefreshDataRepository;
    private final InspireProjectRepository inspireProjectRepository;
    private final InspireOmProjectRepository inspireOmProjectRepository;
    private final InspireOmLinesRepository inspireOmLinesRepository;
    private final LogRepository logRepository;

    @Autowired
    public InspireService(InspirePositionToActivityTempRepository inspirePositionToActivityTempRepository,
                          InspireSpirRefreshDataRepository inspireSpirRefreshDataRepository,
                          InspireProjectRepository inspireProjectRepository,
                          InspireOmProjectRepository inspireOmProjectRepository,
                          InspireOmLinesRepository inspireOmLinesRepository,
                          LogRepository logRepository) {
        this.inspirePositionToActivityTempRepository = inspirePositionToActivityTempRepository;
        this.inspireSpirRefreshDataRepository = inspireSpirRefreshDataRepository;
        this.inspireProjectRepository = inspireProjectRepository;
        this.inspireOmProjectRepository = inspireOmProjectRepository;
        this.inspireOmLinesRepository = inspireOmLinesRepository;
        this.logRepository = logRepository;
    }

    @Transactional
    public void deleteInspireProject(Long spirProjectId, Long logId) {
        if (spirProjectId == null || logId == null) {
            throw new IllegalArgumentException("spirProjectId and logId cannot be null");
        }

        try {
            // Delete rows from inspire_position_to_activity_temp
            List<InspirePositionToActivityTemp> inspirePositionToActivityTemps = inspirePositionToActivityTempRepository.findBySpirProjectId(spirProjectId);
            inspirePositionToActivityTempRepository.deleteAll(inspirePositionToActivityTemps);

            // Update inspire_spir_refresh_data
            InspireSpirRefreshData inspireSpirRefreshData = inspireSpirRefreshDataRepository.findBySpirProjectId(spirProjectId);
            if (inspireSpirRefreshData != null) {
                inspireSpirRefreshData.setProcessed("Y");
                inspireSpirRefreshDataRepository.save(inspireSpirRefreshData);
            }

            // Update inspire_project
            InspireProject inspireProject = inspireProjectRepository.findBySpirProjectId(spirProjectId);
            if (inspireProject != null) {
                inspireProject.setSpirStatus("COMPLETED");
                inspireProjectRepository.save(inspireProject);
            }

            // Update inspire_om_project
            InspireOmProject inspireOmProject = inspireOmProjectRepository.findBySpirProjectIdAndProcessFlag(spirProjectId, "P");
            if (inspireOmProject != null) {
                inspireOmProject.setProcessFlag("E");
                inspireOmProject.setErrorMessage("Forced om callback");
                inspireOmProjectRepository.save(inspireOmProject);
            }

            // Update inspire_om_lines
            List<InspireOmLines> inspireOmLines = inspireOmLinesRepository.findBySpirProjectIdAndProcessed(spirProjectId, "P");
            inspireOmLines.forEach(line -> {
                line.setProcessed("E");
                inspireOmLinesRepository.save(line);
            });

            // Update log entry
            logRepository.updateLogEntry(logId, "COMPLETED", "Inspire project deleted successfully");
        } catch (Exception e) {
            logRepository.updateLogEntry(logId, "ERROR", "Error deleting inspire project: " + e.getMessage());
            // Roll back the transaction
            throw new RuntimeException(e);
        }
    }
}