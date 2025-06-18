package com.example.exception;

public class ProjectServiceException extends RuntimeException {
    
    public ProjectServiceException() {
        super();
    }
    
    public ProjectServiceException(String message) {
        super(message);
    }
    
    public ProjectServiceException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ProjectServiceException(Throwable cause) {
        super(cause);
    }
}