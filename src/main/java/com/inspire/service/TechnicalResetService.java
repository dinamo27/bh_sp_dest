package com.inspire.service;

public interface TechnicalResetService {
    /**
     * Performs a technical reset on an Inspire project.
     * @param projectId The ID of the project to reset
     * @return 0 for success, -1 for failure
     */
    int resetProject(String projectId);
}