package com.example.repository;

import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TechnicalResetRepository {
    /**
     * Executes the technical reset procedure for a project.
     * This procedure handles all aspects of resetting a stalled project:
     * - Updates records in multiple tables to mark them as processed
     * - Deletes temporary mapping records
     * - Updates project status to COMPLETED
     * - Handles order management records
     * - Creates comprehensive audit logs
     *
     * @param projectId The unique identifier of the project to reset
     * @return 0 if successful, -1 if an error occurred
     */
    @Transactional(rollbackFor = Exception.class)
    @Procedure(procedureName = "technical_reset_project")
    Integer executeResetProject(@Param("project_id") String projectId);
}