package com.inspire.service;

public interface ProjectResetService {
    /**
     * Resets an Inspire project by setting its status to COMPLETED and updating various related tables and statuses.
     * 
     * @param projectId The ID of the project to reset
     * @return 0 for success, -1 for failure
     */
    int resetProject(String projectId);
}