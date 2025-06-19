package com.example.service;

/**
 * Service interface for performing technical resets of Inspire projects.
 */
public interface TechnicalResetService {
    
    /**
     * Resets a project with the given ID.
     *
     * @param projectId the ID of the project to reset
     * @return 0 for success, -1 for failure
     */
    int resetProject(String projectId);
}