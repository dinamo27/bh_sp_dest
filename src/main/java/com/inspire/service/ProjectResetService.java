package com.inspire.service;

public interface ProjectResetService {
    /**
     * Resets and finalizes a project by updating its status to COMPLETED and
     * forcing callbacks on related Order Management records.
     *
     * @param projectId The ID of the project to reset and finalize
     * @return true if the operation was successful, false otherwise
     */
    boolean resetAndFinalizeProject(Integer projectId);
}