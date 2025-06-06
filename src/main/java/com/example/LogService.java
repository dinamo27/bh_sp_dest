

package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.logging.Level;

import javax.validation.constraints.NotNull;

@Service
public class LogService {

    private static final Logger LOGGER = Logger.getLogger(LogService.class.getName());

    @Autowired
    private LogRepository logRepository;

    /**
     * Creates a new log entry.
     * 
     * @param processName the name of the process
     * @param projectId   the ID of the project
     * @return the created log entry
     */
    public Log createLogEntry(@NotNull String processName, @NotNull ProjectId projectId) {
        if (processName == null || processName.isEmpty()) {
            LOGGER.log(Level.SEVERE, "Process name cannot be null or empty");
            throw new IllegalArgumentException("Process name cannot be null or empty");
        }

        if (projectId == null) {
            LOGGER.log(Level.SEVERE, "Project ID cannot be null");
            throw new IllegalArgumentException("Project ID cannot be null");
        }

        Log log = new Log();
        log.setProcessName(processName);
        log.setProjectId(projectId);
        log.setLogId(UUID.randomUUID().toString());
        try {
            logRepository.save(log);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error saving log entry", e);
            throw new RuntimeException("Error saving log entry", e);
        }
        return log;
    }

    /**
     * Logs the outcome of a process.
     * 
     * @param log     the log entry to update
     * @param status  the status of the process
     * @param message the message to log
     */
    public void logOutcome(@NotNull Log log, @NotNull String status, @NotNull String message) {
        if (log == null) {
            LOGGER.log(Level.SEVERE, "Log entry cannot be null");
            throw new IllegalArgumentException("Log entry cannot be null");
        }

        if (status == null || status.isEmpty()) {
            LOGGER.log(Level.SEVERE, "Status cannot be null or empty");
            throw new IllegalArgumentException("Status cannot be null or empty");
        }

        if (message == null || message.isEmpty()) {
            LOGGER.log(Level.SEVERE, "Message cannot be null or empty");
            throw new IllegalArgumentException("Message cannot be null or empty");
        }

        log.setStatus(status);
        log.setMessage(message);
        try {
            logRepository.save(log);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error saving log entry", e);
            throw new RuntimeException("Error saving log entry", e);
        }
    }
}