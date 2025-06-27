package com.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProjectResetService {
    private final JdbcTemplate jdbcTemplate;
    private static final Logger logger = LoggerFactory.getLogger(ProjectResetService.class);
    
    @Autowired
    public ProjectResetService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public void resetProject(Long projectId) {
        if (projectId == null) {
            throw new IllegalArgumentException("Project ID cannot be null");
        }
        
        logger.info("Starting reset operation for project ID: {}", projectId);
        
        try {
            jdbcTemplate.update("CALL update.inspire_om_project(?)", projectId);
            
            logger.info("Successfully reset project ID: {}", projectId);
        } catch (Exception e) {
            logger.error("Error resetting project ID: {}", projectId, e);
            throw new ProjectResetException("Failed to reset project with ID: " + projectId, e);
        }
    }
    
    public static class ProjectResetException extends RuntimeException {
        public ProjectResetException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}