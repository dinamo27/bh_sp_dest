package com.inspire.service;

/**
 * Service interface for project-related operations.
 */
public interface ProjectService {
    
    /**
     * Performs a technical reset of a project, which includes:
     * - Marking records in parts_grouped_recalc as processed
     * - Marking records in spir_refresh_data as processed
     * - Deleting records in temp_pos_to_activity
     * - Setting error flags on unprocessed OM project and line records
     * - Updating project status to COMPLETED
     *
     * @param projectId The ID of the project to reset
     * @return true if the reset was successful, false otherwise
     * @throws IllegalArgumentException if the project does not exist
     */
    boolean technicalResetProject(Long projectId);
}