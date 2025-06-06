package com.example.project.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
public class LogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    @Column
    private Long spirProjectId;

    @Column
    @Enumerated(EnumType.STRING)
    private LogStatus status;

    @Column
    private String logMessage;

    @Column
    @Enumerated(EnumType.STRING)
    private LogType logType;

    public LogEntry() {}

    public LogEntry(Long spirProjectId, LogStatus status, String logMessage, LogType logType) {
        this.spirProjectId = spirProjectId;
        this.status = status;
        this.logMessage = logMessage;
        this.logType = logType;
    }

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public Long getSpirProjectId() {
        return spirProjectId;
    }

    public void setSpirProjectId(Long spirProjectId) {
        this.spirProjectId = spirProjectId;
    }

    public LogStatus getStatus() {
        return status;
    }

    public void setStatus(LogStatus status) {
        this.status = status;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }

    public LogType getLogType() {
        return logType;
    }

    public void setLogType(LogType logType) {
        this.logType = logType;
    }
}

enum LogStatus {
    COMPLETED,
    ERROR
}

enum LogType {
    INFO,
    WARNING,
    ERROR
}