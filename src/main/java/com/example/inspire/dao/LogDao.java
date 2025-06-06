

package com.example.inspire.dao;

import java.util.Date;

public class LogDao {

    public void createLogEntry(Integer projectId, String processName) {
        Log log = new Log();
        log.setLogId(generateUniqueLogId());
        log.setProjectId(projectId);
        log.setProcessName(processName);
        log.setStartDate(new Date());
        LogRepository.logRepository.save(log);
    }

    public void logOutcome(Integer projectId, String status) {
        Log log = LogRepository.getLogEntry(projectId);
        if (log != null) {
            log.setEndDate(new Date());
            log.setStatus(status);
            log.setMessage("MESSAGE");
            LogRepository.logRepository.save(log);
        }
    }

    private String generateUniqueLogId() {
        return String.valueOf(System.currentTimeMillis());
    }
}

class Log {
    private String logId;
    private Integer projectId;
    private String processName;
    private Date startDate;
    private Date endDate;
    private String status;
    private String message;

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

class LogRepository {
    public static LogRepository logRepository = new LogRepository();

    public void save(Log log) {
        // implement save logic
    }

    public Log getLogEntry(Integer projectId) {
        // implement get log entry logic
        return null;
    }
}