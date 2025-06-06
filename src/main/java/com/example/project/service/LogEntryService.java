package com.example.project.service;

import com.example.project.entity.LogEntry;
import com.example.project.repository.LogEntryRepository;
import org.springframework.stereotype.Service;

@Service
public class LogEntryService {

    private final LogEntryRepository logEntryRepository;

    public LogEntryService(LogEntryRepository logEntryRepository) {
        this.logEntryRepository = logEntryRepository;
    }

    public void updateLogEntry(Long logId, String logMessage) {
        logEntryRepository.updateLogEntry(logId, logMessage);
    }
}