package com.inspire.exception;

public class ProjectResetException extends RuntimeException {
    private final Integer projectId;
    
    public ProjectResetException(String message, Integer projectId) {
        super(message);
        this.projectId = projectId;
    }
    
    public ProjectResetException(String message, Throwable cause, Integer projectId) {
        super(message, cause);
        this.projectId = projectId;
    }
    
    public Integer getProjectId() {
        return projectId;
    }
}