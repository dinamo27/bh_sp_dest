

package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResetProjectEndpoint {

    private static final Logger LOGGER = Logger.getLogger(ResetProjectEndpoint.class.getName());

    public int resetProject(int projectId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int logId = 0;
        int successFlag = 0;
        int rowCount = 0;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "username", "password");
            conn.setAutoCommit(false);

            // Validate project_id
            pstmt = conn.prepareStatement("SELECT COUNT(*) FROM inspire_project WHERE spir_project_id = ?");
            pstmt.setInt(1, projectId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                rowCount = rs.getInt(1);
                if (rowCount == 0) {
                    throw new SQLException("Invalid project_id");
                }
            }

            // Create log entry for tech_reset_project process
            pstmt = conn.prepareStatement("INSERT INTO inspire_log (process_name, status, message) VALUES ('tech_reset_project', 'STARTED', 'Reset project process started')");
            pstmt.executeUpdate();

            // Get log id
            pstmt = conn.prepareStatement("SELECT LAST_INSERT_ID()");
            rs = pstmt.executeQuery();
            if (rs.next()) {
                logId = rs.getInt(1);
            }

            // Update inspire_parts_grouped_recalc table
            pstmt = conn.prepareStatement("UPDATE inspire_parts_grouped_recalc SET processed = 'Y' WHERE spir_project_id = ?");
            pstmt.setInt(1, projectId);
            rowCount = pstmt.executeUpdate();

            // Delete from inspire_position_to_activity_temp table
            pstmt = conn.prepareStatement("DELETE FROM inspire_position_to_activity_temp WHERE spir_project_id = ?");
            pstmt.setInt(1, projectId);
            rowCount += pstmt.executeUpdate();

            // Update inspire_spir_refresh_data table
            pstmt = conn.prepareStatement("UPDATE inspire_spir_refresh_data SET processed = 'Y' WHERE spir_project_id = ?");
            pstmt.setInt(1, projectId);
            rowCount += pstmt.executeUpdate();

            // Update inspire_project table
            pstmt = conn.prepareStatement("UPDATE inspire_project SET spir_status = 'COMPLETED' WHERE spir_project_id = ?");
            pstmt.setInt(1, projectId);
            rowCount += pstmt.executeUpdate();

            // Update inspire_om_project table
            pstmt = conn.prepareStatement("UPDATE inspire_om_project SET process_flag = 'E', error_message = 'Forced om callback' WHERE spir_project_id = ? AND process_flag = 'P'");
            pstmt.setInt(1, projectId);
            rowCount += pstmt.executeUpdate();

            // Update inspire_om_lines table
            pstmt = conn.prepareStatement("UPDATE inspire_om_lines SET processed = 'E' WHERE spir_project_id = ? AND processed = 'P'");
            pstmt.setInt(1, projectId);
            rowCount += pstmt.executeUpdate();

            // Log outcome of process
            pstmt = conn.prepareStatement("UPDATE inspire_log SET status = 'COMPLETED', message = 'Reset project process completed', details = 'Project ID: " + projectId + ", Row count: " + rowCount + "' WHERE log_id = ?");
            pstmt.setInt(1, logId);
            pstmt.executeUpdate();

            conn.commit();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            successFlag = -1;
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex1) {
                    LOGGER.log(Level.SEVERE, null, ex1);
                }
            }
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }
        }

        return successFlag;
    }
}