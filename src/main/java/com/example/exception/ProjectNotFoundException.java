package com.example.exception;

public class ProjectNotFoundException extends RuntimeException {

    public ProjectNotFoundException(String message) {
        super(message);
    }

    public ProjectNotFoundException(Integer projectId) {
        super("Project with ID " + projectId + " not found");
    }

    public ProjectNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}