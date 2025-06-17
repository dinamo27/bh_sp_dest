package com.inspire.dto;

public class ProcessingResult {
    private int affectedRows;
    private long executionTimeMs;
    private boolean success;
    private String errorMessage;

    public ProcessingResult() {
    }

    public ProcessingResult(int affectedRows, long executionTimeMs) {
        this.affectedRows = affectedRows;
        this.executionTimeMs = executionTimeMs;
        this.success = true;
    }

    public ProcessingResult(String errorMessage) {
        this.errorMessage = errorMessage;
        this.success = false;
    }

    public int getAffectedRows() {
        return affectedRows;
    }

    public void setAffectedRows(int affectedRows) {
        this.affectedRows = affectedRows;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "ProcessingResult{" +
                "affectedRows=" + affectedRows +
                ", executionTimeMs=" + executionTimeMs +
                ", success=" + success +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}