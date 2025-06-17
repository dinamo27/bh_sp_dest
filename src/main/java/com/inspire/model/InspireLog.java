package com.inspire.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "inspire_log")
public class InspireLog {

    @Id
    @Column(name = "log_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    @Column(name = "log_date")
    private LocalDateTime logDate;

    @Column(name = "log_type")
    private String logType;

    @Column(name = "log_message")
    private String logMessage;

    @Column(name = "procedure_name")
    private String procedureName;

    @Column(name = "affected_rows")
    private Integer affectedRows;

    @Column(name = "execution_time")
    private Double executionTime;

    public InspireLog() {
    }

    public InspireLog(LocalDateTime logDate, String logType, String logMessage, String procedureName, Integer affectedRows, Double executionTime) {
        this.logDate = logDate;
        this.logType = logType;
        this.logMessage = logMessage;
        this.procedureName = procedureName;
        this.affectedRows = affectedRows;
        this.executionTime = executionTime;
    }

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public LocalDateTime getLogDate() {
        return logDate;
    }

    public void setLogDate(LocalDateTime logDate) {
        this.logDate = logDate;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

    public Integer getAffectedRows() {
        return affectedRows;
    }

    public void setAffectedRows(Integer affectedRows) {
        this.affectedRows = affectedRows;
    }

    public Double getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Double executionTime) {
        this.executionTime = executionTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InspireLog that = (InspireLog) o;
        return Objects.equals(logId, that.logId) &&
                Objects.equals(logDate, that.logDate) &&
                Objects.equals(logType, that.logType) &&
                Objects.equals(logMessage, that.logMessage) &&
                Objects.equals(procedureName, that.procedureName) &&
                Objects.equals(affectedRows, that.affectedRows) &&
                Objects.equals(executionTime, that.executionTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(logId, logDate, logType, logMessage, procedureName, affectedRows, executionTime);
    }

    @Override
    public String toString() {
        return "InspireLog{" +
                "logId=" + logId +
                ", logDate=" + logDate +
                ", logType='" + logType + '\'' +
                ", logMessage='" + logMessage + '\'' +
                ", procedureName='" + procedureName + '\'' +
                ", affectedRows=" + affectedRows +
                ", executionTime=" + executionTime +
                '}';
    }
}