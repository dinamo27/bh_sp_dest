package com.example.repository;

public interface ProjectRepositoryCustom {
    /**
     * Resets and finalizes a project by updating its status to COMPLETED,
     * forcing callbacks on related Order Management records,
     * and handling related records in associated tables.
     *
     * @param projectId The ID of the project to reset and finalize
     * @return 0 for success, -1 for failure
     */
    Integer resetAndFinalizeProject(Long projectId);
}