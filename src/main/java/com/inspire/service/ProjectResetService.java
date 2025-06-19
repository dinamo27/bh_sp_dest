package com.inspire.service;

public interface ProjectResetService {
    
    /**
     * Performs a technical reset of an Inspire project.
     * 
     * @param projectId The ID of the project to reset
     * @return 0 if the reset was successful, -1 if an error occurred
     */
    int resetProject(String projectId);
}