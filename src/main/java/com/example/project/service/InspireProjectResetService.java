package com.example.project.service;

import com.example.project.model.ResetResult;

/**
 * Service interface for resetting inspire projects
 */
public interface InspireProjectResetService {
    
    /**
     * Resets an inspire OM project by marking pending records as errored
     * @param spirProjectId The ID of the project to reset
     * @param logId The ID of the log entry to update
     * @return ResetResult containing success flag and message
     */
    ResetResult resetInspireOmProject(Long spirProjectId, Long logId);
}