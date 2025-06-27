package com.example.project.service;

/**
 * Interface defining logging operations for the application.
 */
public interface LoggingService {
    
    /**
     * Updates a log entry with the given status and message
     * 
     * @param logId The ID of the log entry to update
     * @param status The status to set (e.g., 'COMPLETED', 'ERROR')
     * @param message The message to set
     */
    void updateLogEntry(Long logId, String status, String message);
    
    /**
     * Adds log details with various counters and metrics
     * 
     * @param logId The ID of the log entry these details relate to
     * @param spirProjectId The ID of the project
     * @param temp1 First counter value
     * @param temp2 Second counter value
     * @param temp3 Third counter value
     * @param temp4 Fourth counter value
     * @param temp5 Fifth counter value
     * @param rowCount Number of rows affected
     */
    void addLogDetails(Long logId, Long spirProjectId, Integer temp1, Integer temp2, 
                      Integer temp3, Integer temp4, Integer temp5, Integer rowCount);
}