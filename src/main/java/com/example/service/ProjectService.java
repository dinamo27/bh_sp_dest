package com.example.service;

public interface ProjectService {
    /**
     * Marks a project as failed by updating its status
     * @param projectId The ID of the project to mark as failed
     * @return true if the update was successful, false otherwise
     */
    boolean markProjectAsFailed(Long projectId);
}