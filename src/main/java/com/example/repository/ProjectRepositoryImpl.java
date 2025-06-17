package com.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ProjectRepositoryImpl implements ProjectRepositoryCustom {
    
    private static final Logger logger = LoggerFactory.getLogger(ProjectRepositoryImpl.class);
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    @Transactional
    public Integer resetAndFinalizeProject(Integer projectId) {
        if (projectId == null) {
            logger.error("Project ID cannot be null");
            return -1;
        }
        
        logger.info("Starting project reset and finalization for project ID: {}", projectId);
        
        try {
            // 1. Update project status to COMPLETED
            String updateProjectSql = "UPDATE inspire_project SET status = 'COMPLETED' WHERE project_id = :projectId";
            Query updateProjectQuery = entityManager.createNativeQuery(updateProjectSql)
                    .setParameter("projectId", projectId);
            int projectRowsUpdated = updateProjectQuery.executeUpdate();
            logger.info("Updated project status to COMPLETED. Rows affected: {}", projectRowsUpdated);
            
            if (projectRowsUpdated == 0) {
                logger.warn("No project found with ID: {}", projectId);
                return -1;
            }
            
            // 2. Update refresh data status
            String updateRefreshDataSql = "UPDATE inspire_spir_refresh_data SET status = 'E' WHERE project_id = :projectId";
            Query updateRefreshDataQuery = entityManager.createNativeQuery(updateRefreshDataSql)
                    .setParameter("projectId", projectId);
            int refreshDataRowsUpdated = updateRefreshDataQuery.executeUpdate();
            logger.info("Updated refresh data status. Rows affected: {}", refreshDataRowsUpdated);
            
            // 3. Force callbacks on OM projects
            String updateOmProjectsSql = "UPDATE inspire_om_project SET process_flag = 'E', error_message = 'Forced om callback' " +
                    "WHERE project_id = :projectId AND (process_flag = 'P' OR process_flag IS NULL)";
            Query updateOmProjectsQuery = entityManager.createNativeQuery(updateOmProjectsSql)
                    .setParameter("projectId", projectId);
            int omProjectsRowsUpdated = updateOmProjectsQuery.executeUpdate();
            logger.info("Updated OM projects with error status. Rows affected: {}", omProjectsRowsUpdated);
            
            // 4. Mark OM line items as processed with error
            String updateOmLinesSql = "UPDATE inspire_om_lines SET processed = 'E' " +
                    "WHERE project_id = :projectId AND (processed = 'P' OR processed IS NULL)";
            Query updateOmLinesQuery = entityManager.createNativeQuery(updateOmLinesSql)
                    .setParameter("projectId", projectId);
            int omLinesRowsUpdated = updateOmLinesQuery.executeUpdate();
            logger.info("Updated OM line items with error status. Rows affected: {}", omLinesRowsUpdated);
            
            logger.info("Project reset and finalization completed successfully for project ID: {}", projectId);
            return 0; // Success
        } catch (Exception e) {
            logger.error("Failed to reset and finalize project with ID: {}", projectId, e);
            // Transaction will be rolled back automatically due to @Transactional annotation
            return -1; // Failure
        }
    }
}