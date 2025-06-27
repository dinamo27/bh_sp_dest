package com.example.project.service;

import com.example.project.repository.InspireOmProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class InspireOmProjectService {

    private final InspireOmProjectRepository inspireOmProjectRepository;
    private final InspireOmLineService inspireOmLineService;
    private static final Logger logger = LoggerFactory.getLogger(InspireOmProjectService.class);

    public InspireOmProjectService(InspireOmProjectRepository inspireOmProjectRepository, InspireOmLineService inspireOmLineService) {
        this.inspireOmProjectRepository = inspireOmProjectRepository;
        this.inspireOmLineService = inspireOmLineService;
    }

    public Map<String, Object> updateInspireOmProjectWithErrorStatus(Long spirProjectId, Long logId, String temp1, String temp2, String temp3, String temp4) {
        int successFlag = 0;
        String returnMessage = null;
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Update project records to error status
            int temp5 = inspireOmProjectRepository.updateProcessFlagForPendingRecordsByProjectId(spirProjectId);
            
            // Update line records to error status
            inspireOmLineService.updateInspireOmLinesWithErrorStatus(spirProjectId, logId, temp1, temp2, temp3, temp4, String.valueOf(temp5));
            
            // Log completion
            logger.info("Completed updating project and line records for project ID: {}", spirProjectId);
            
            // Log details
            logger.info("Update details - Project ID: {}, Log ID: {}, Affected projects: {}, Parameters: {}, {}, {}, {}", 
                spirProjectId, logId, temp5, temp1, temp2, temp3, temp4);
            
        } catch (Exception e) {
            successFlag = -1;
            returnMessage = e.getMessage();
            
            // Log error
            logger.error("Error updating project and line records for project ID: {}", spirProjectId, e);
        }
        
        result.put("successFlag", successFlag);
        result.put("returnMessage", returnMessage);
        
        return result;
    }
}