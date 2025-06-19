package com.example.service.impl;

import com.example.model.LogDetail;
import com.example.model.LogEntry;
import com.example.repository.LogDetailRepository;
import com.example.repository.LogEntryRepository;
import com.example.service.LoggingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LoggingServiceImpl implements LoggingService {

    private final LogEntryRepository logEntryRepository;
    private final LogDetailRepository logDetailRepository;
    private final Logger logger = LoggerFactory.getLogger(LoggingServiceImpl.class);

    @Autowired
    public LoggingServiceImpl(LogEntryRepository logEntryRepository, LogDetailRepository logDetailRepository) {
        this.logEntryRepository = logEntryRepository;
        this.logDetailRepository = logDetailRepository;
    }

    @Override
    public Long createLogEntry(String source, String type, String message, String userId) {
        LogEntry logEntry = new LogEntry();
        logEntry.setSource(source);
        logEntry.setType(type);
        logEntry.setMessage(message);
        logEntry.setUserId(userId);
        logEntry.setCreatedAt(LocalDateTime.now());
        
        LogEntry savedEntry = logEntryRepository.save(logEntry);
        logger.info("Created log entry: {} - {} - {}", source, type, message);
        
        return savedEntry.getId();
    }

    @Override
    public void updateLogEntry(Long logId, String status, String result) {
        LogEntry logEntry = logEntryRepository.findById(logId)
                .orElseThrow(() -> new IllegalArgumentException("Log entry not found with ID: " + logId));
        
        logEntry.setStatus(status);
        logEntry.setResult(result);
        logEntry.setUpdatedAt(LocalDateTime.now());
        
        logEntryRepository.save(logEntry);
        logger.info("Updated log entry {}: status={}, result={}", logId, status, result);
    }

    @Override
    public void addLogDetail(Long logId, String key, String value) {
        LogEntry logEntry = logEntryRepository.findById(logId)
                .orElseThrow(() -> new IllegalArgumentException("Log entry not found with ID: " + logId));
        
        LogDetail logDetail = new LogDetail();
        logDetail.setLogEntry(logEntry);
        logDetail.setDetailKey(key);
        logDetail.setDetailValue(value);
        logDetail.setCreatedAt(LocalDateTime.now());
        
        logDetailRepository.save(logDetail);
        logger.debug("Added log detail to entry {}: {}={}", logId, key, value);
    }
}