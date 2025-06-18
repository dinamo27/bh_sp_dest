package com.example.service;

/**
 * Service interface defining the contract for project reset operations.
 */
public interface ProjectResetService {
    
    /**
     * Performs a technical reset of a project.
     * 
     * @param projectId The ID of the project to reset
     * @return Integer status code: 0 for success, -1 for failure
     * @throws IllegalArgumentException if the project ID is invalid
     */
    Integer technicalResetProject(Integer projectId);
}