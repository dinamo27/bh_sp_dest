

package com.example.inspire.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.logging.Logger;

@Repository
public class InspireRepository {

    private final JdbcTemplate jdbcTemplate;
    private final LogDao logDao;
    private final Logger logger;

    public InspireRepository(DataSource dataSource, LogDao logDao) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.logDao = logDao;
        this.logger = Logger.getLogger(InspireRepository.class.getName());
    }

    public boolean resetProject(Integer projectId) {
        if (projectId == null) {
            throw new IllegalArgumentException("Project ID cannot be null");
        }

        Log log = new Log();
        log.setLogId(getUniqueLogId());
        log.setProjectId(projectId);
        logDao.createLogEntry(projectId, "tech_reset_project");

        try {
            updateInspirePartsGroupedRecalc(projectId);
            deleteInspirePositionToActivityTemp(projectId);
            updateInspireSpirRefreshData(projectId);
            updateInspireProject(projectId);
            updateInspireOmProject(projectId);
            updateInspireOmLines(projectId);

            log.setStatus("COMPLETED");
            log.setMessage("MESSAGE");
            log.setRowCount(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM inspire_om_lines WHERE spir_project_id = ?", Integer.class, projectId));
            logDao.save(log);
            logDao.logOutcome(projectId, "COMPLETED");
            return true;
        } catch (Exception e) {
            log.setStatus("FAILED");
            log.setMessage(e.getMessage());
            logDao.save(log);
            logDao.logOutcome(projectId, "FAILED");
            return false;
        }
    }

    private Integer getUniqueLogId() {
        Integer maxLogId = jdbcTemplate.queryForObject("SELECT MAX(log_id) FROM log", Integer.class);
        return maxLogId == null ? 1 : maxLogId + 1;
    }

    private void updateInspirePartsGroupedRecalc(Integer projectId) {
        String updateQuery = "UPDATE inspire_parts_grouped_recalc SET processed = 'Y' WHERE spir_project_id = ? ";
        jdbcTemplate.update(updateQuery, projectId);
    }

    private void deleteInspirePositionToActivityTemp(Integer projectId) {
        String deleteQuery = " DELETE FROM inspire_position_to_activity_temp WHERE spir_project_id = ? ";
        jdbcTemplate.update(deleteQuery, projectId);
    }

    private void updateInspireSpirRefreshData(Integer projectId) {
        String updateQuery = " UPDATE inspire_spir_refresh_data SET processed = 'Y' WHERE spir_project_id = ? ";
        jdbcTemplate.update(updateQuery, projectId);
    }

    private void updateInspireProject(Integer projectId) {
        String updateQuery = " UPDATE inspire_project SET spir_status = 'COMPLETED' WHERE spir_project_id = ? ";
        jdbcTemplate.update(updateQuery, projectId);
    }

    private void updateInspireOmProject(Integer projectId) {
        String updateQuery = " UPDATE inspire_om_project SET process_flag = 'E', error_message = 'Forced om callback' WHERE spir_project_id = ? AND process_flag = 'P' ";
        jdbcTemplate.update(updateQuery, projectId);
    }

    private void updateInspireOmLines(Integer projectId) {
        String updateQuery = " UPDATE inspire_om_lines SET processed = 'E' WHERE spir_project_id = ? AND processed = 'P' ";
        jdbcTemplate.update(updateQuery, projectId);
    }
}