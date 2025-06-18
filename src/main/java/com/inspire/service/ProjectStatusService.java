package com.inspire.service;

public interface ProjectStatusService {
    /**
     * Marks a project as failed in the database
     * @param projectId The unique identifier of the project to mark as failed
     * @return 0 for success, -1 for error
     */
    int markProjectAsFailed(Integer projectId);
}