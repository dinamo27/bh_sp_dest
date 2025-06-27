package com.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.project.repository.InspireOmLineRepository;

import java.util.HashMap;
import java.util.Map;

@Service
public class InspireOmLineService {

    private final InspireOmLineRepository inspireOmLineRepository;
    private final Logger logger;

    @Autowired
    public InspireOmLineService(InspireOmLineRepository inspireOmLineRepository) {
        this.inspireOmLineRepository = inspireOmLineRepository;
        this.logger = LoggerFactory.getLogger(InspireOmLineService.class);
    }

    public Map<String, Object> updateInspireOmLinesWithErrorStatus(Long spirProjectId, Long logId, 
                                                                 String temp1, String temp2, 
                                                                 String temp3, String temp4, 
                                                                 String temp5) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        
        try {
            if (spirProjectId == null) {
                throw new IllegalArgumentException("Project ID cannot be null");
            }
            
            int updatedCount = inspireOmLineRepository.updateProcessedStatusForUnprocessedRecordsByProjectId(spirProjectId);
            
            logger.info("Updated {} unprocessed records to error status for project ID: {}", updatedCount, spirProjectId);
            logger.info("Operation details - logId: {}, temp1: {}, temp2: {}, temp3: {}, temp4: {}, temp5: {}", 
                    logId, temp1, temp2, temp3, temp4, temp5);
            
            result.put("success", true);
            result.put("updatedCount", updatedCount);
            result.put("message", "Successfully updated unprocessed records to error status");
            
        } catch (Exception e) {
            logger.error("Error updating inspire_om_lines to error status for project ID: " + spirProjectId, e);
            
            result.put("success", false);
            result.put("message", "Failed to update records: " + e.getMessage());
            result.put("error", e.getClass().getName());
        }
        
        return result;
    }
}