package com.example.service;

public interface ProjectService {
    
    /**
     * Marks a project as failed by updating its status
     * 
     * @param projectId the ID of the project to mark as failed
     * @return true if the project was successfully marked as failed, false otherwise
     */
    boolean markProjectAsFailed(Long projectId);
}