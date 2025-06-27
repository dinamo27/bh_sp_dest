package com.example.project.service;

import com.example.project.dto.OperationResult;
import com.example.project.repository.InspireOmLineRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class InspireOmLineService {
    private final InspireOmLineRepository inspireOmLineRepository;
    private final Logger logger;

    public InspireOmLineService(InspireOmLineRepository inspireOmLineRepository) {
        this.inspireOmLineRepository = inspireOmLineRepository;
        this.logger = LoggerFactory.getLogger(InspireOmLineService.class);
    }

    public OperationResult updateInspireOmLinesForProjectReset(String spirProjectId, Long logId, String errorMessage, 
                                                              String temp1, String temp2, String temp3, 
                                                              String temp4, String temp5) {
        int successFlag = 0;
        String returnMessage = null;

        logger.info("Updating unprocessed inspire_om_lines to error status for project: {}", spirProjectId);

        try {
            int affectedRows = inspireOmLineRepository.updateUnprocessedOmLinesToError(spirProjectId, errorMessage);

            logger.info("Successfully updated inspire_om_lines for project: {} with {} affected rows. Details: {}, {}, {}, {}, {}", 
                        spirProjectId, affectedRows, temp1, temp2, temp3, temp4, temp5);
        } catch (Exception e) {
            successFlag = -1;
            returnMessage = e.getMessage();

            logger.error("Error updating inspire_om_lines for project: {}. Error: {}", spirProjectId, e.getMessage(), e);
        }

        return new OperationResult(successFlag, returnMessage);
    }
}