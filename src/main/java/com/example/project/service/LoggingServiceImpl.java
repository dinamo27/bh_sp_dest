package com.example.project.service;

import com.example.project.entity.LogDetail;
import com.example.project.entity.LogEntry;
import com.example.project.repository.LogDetailRepository;
import com.example.project.repository.LogEntryRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoggingServiceImpl implements LoggingService {

    private final LogEntryRepository logEntryRepository;
    private final LogDetailRepository logDetailRepository;

    public LoggingServiceImpl(LogEntryRepository logEntryRepository, LogDetailRepository logDetailRepository) {
        this.logEntryRepository = logEntryRepository;
        this.logDetailRepository = logDetailRepository;
    }

    @Override
    public void updateLogEntry(Long logId, String status, String message) {
        Optional<LogEntry> logEntryOptional = logEntryRepository.findById(logId);
        if (logEntryOptional.isPresent()) {
            LogEntry logEntry = logEntryOptional.get();
            logEntry.setStatus(status);
            logEntry.setMessage(message);
            logEntryRepository.save(logEntry);
        }
    }

    @Override
    public void addLogDetails(Long logId, Long spirProjectId, Integer temp1, Integer temp2,
                             Integer temp3, Integer temp4, Integer temp5, Integer rowCount) {
        LogDetail logDetail = new LogDetail();
        logDetail.setLogId(logId);
        logDetail.setSpirProjectId(spirProjectId);
        logDetail.setTemp1(temp1);
        logDetail.setTemp2(temp2);
        logDetail.setTemp3(temp3);
        logDetail.setTemp4(temp4);
        logDetail.setTemp5(temp5);
        logDetail.setRowCount(rowCount);
        logDetailRepository.save(logDetail);
    }
}