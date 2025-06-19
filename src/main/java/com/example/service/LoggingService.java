package com.example.service;

import java.time.LocalDateTime;

public interface LoggingService {
    
    /**
     * Creates a log entry for an operation
     * 
     * @param projectId the ID of the project
     * @param operation the operation being performed
     * @param status the status of the operation
     * @param startTime the start time of the operation
     * @param message a message describing the operation
     * @return the ID of the created log entry
     */
    Long createLogEntry(Long projectId, String operation, String status, LocalDateTime startTime, String message);
    
    /**
     * Updates an existing log entry
     * 
     * @param logId the ID of the log entry to update
     * @param status the new status of the operation
     * @param endTime the end time of the operation
     * @param affectedRows the number of rows affected by the operation
     * @param message a message describing the result of the operation
     */
    void updateLogEntry(Long logId, String status, LocalDateTime endTime, Integer affectedRows, String message);
    
    /**
     * Adds a detail to an existing log entry
     * 
     * @param logId the ID of the log entry to add the detail to
     * @param operation the operation being performed
     * @param affectedRows the number of rows affected by the operation
     * @param message a message describing the detail
     */
    void addLogDetail(Long logId, String operation, Integer affectedRows, String message);
}