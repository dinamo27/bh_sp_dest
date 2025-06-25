package com.inspire.service;

public interface SpirProjectService {
    
    /**
     * Resets and finalizes a SPIR project by its ID
     * 
     * @param projectId the ID of the project to reset and finalize
     * @return the number of affected records
     */
    int resetAndFinalizeProject(Long projectId);
}