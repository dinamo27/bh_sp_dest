package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.model.InspireProject;

@Repository
public interface InspireProjectRepository extends JpaRepository<InspireProject, String> {
    
    /**
     * Updates records in inspire_parts_grouped_recalc table to mark them as processed
     * @param projectId the project identifier
     * @return number of affected rows for logging purposes
     */
    @Modifying
    @Query(value = "UPDATE inspire_parts_grouped_recalc SET processed_flag = 'Y' " +
           "WHERE project_id = :projectId AND processed_flag != 'Y'", nativeQuery = true)
    int updateInspirePartsGroupedRecalc(@Param("projectId") String projectId);

    /**
     * Deletes temporary position-to-activity mappings for a project
     * @param projectId the project identifier
     * @return number of affected rows for logging purposes
     */
    @Modifying
    @Query(value = "DELETE FROM inspire_temp_pos_activity_mapping WHERE project_id = :projectId", 
           nativeQuery = true)
    int deleteInspireTempPosActivityMapping(@Param("projectId") String projectId);

    /**
     * Updates records in inspire_spir_refresh table to mark them as processed
     * @param projectId the project identifier
     * @return number of affected rows for logging purposes
     */
    @Modifying
    @Query(value = "UPDATE inspire_spir_refresh SET processed_flag = 'Y' " +
           "WHERE project_id = :projectId AND processed_flag != 'Y'", nativeQuery = true)
    int updateInspireSpirRefresh(@Param("projectId") String projectId);

    /**
     * Updates project status to COMPLETED
     * @param projectId the project identifier
     * @param username the user performing the update
     * @return number of affected rows for logging purposes
     */
    @Modifying
    @Query(value = "UPDATE inspire_project SET status = 'COMPLETED', " +
           "last_update_date = CURRENT_TIMESTAMP, last_updated_by = :username " +
           "WHERE project_id = :projectId AND status != 'COMPLETED'", nativeQuery = true)
    int updateInspireProjectStatus(@Param("projectId") String projectId, @Param("username") String username);

    /**
     * Updates pending OM project records with error status
     * @param projectId the project identifier
     * @return number of affected rows for logging purposes
     */
    @Modifying
    @Query(value = "UPDATE inspire_om_project SET status = 'E', " +
           "error_message = 'Forced om callback' " +
           "WHERE project_id = :projectId AND status = 'P'", nativeQuery = true)
    int updateInspireOmProject(@Param("projectId") String projectId);

    /**
     * Updates pending OM line records with error status
     * @param projectId the project identifier
     * @return number of affected rows for logging purposes
     */
    @Modifying
    @Query(value = "UPDATE inspire_om_lines SET status = 'E', " +
           "error_message = 'Forced om callback' " +
           "WHERE project_id = :projectId AND status = 'P'", nativeQuery = true)
    int updateInspireOmLines(@Param("projectId") String projectId);
    
    /**
     * Executes a technical reset for a stalled Inspire project.
     * This procedure will:
     * 1. Mark records in inspire_parts_grouped_recalc as processed
     * 2. Remove temporary position-to-activity mappings
     * 3. Update SPIR refresh data
     * 4. Update project status to COMPLETED
     * 5. Mark pending OM project records with error
     * 6. Mark pending OM line records with error
     * 7. Create appropriate log entries for all operations
     *
     * @param projectId The ID of the project to reset
     * @return 0 for success, -1 for failure
     */
    @Procedure(name = "technicalResetProject")
    Integer technicalResetProject(@Param("project_id") String projectId);
}