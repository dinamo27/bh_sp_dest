package com.example.project.service;

import com.example.project.entity.InspireLog;
import com.example.project.model.InspireLogUpdateRequest;
import com.example.project.repository.InspireLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.logging.Logger;

@Service
public class InspireLogService {

    private final InspireLogRepository inspireLogRepository;
    private final Logger logger;

    @Autowired
    public InspireLogService(InspireLogRepository inspireLogRepository) {
        this.inspireLogRepository = inspireLogRepository;
        this.logger = Logger.getLogger(InspireLogService.class.getName());
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void updateLogEntry(Long logId, String message, String type) {
        if (logId == null || logId <= 0) {
            logger.severe("Invalid log ID: " + logId);
            return;
        }

        try {
            entityManager.createNativeQuery("CALL inspire.update_log_entry(:logId, :message, :type)")
                    .setParameter("logId", logId)
                    .setParameter("message", message)
                    .setParameter("type", type)
                    .executeUpdate();
        } catch (Exception e) {
            logger.severe("Error updating log entry: " + e.getMessage());
            entityManager.getTransaction().rollback();
            // Update Inspire project procedure
            entityManager.createNativeQuery("CALL inspire.update_project_proc(:logId, :message, :type)")
                    .setParameter("logId", logId)
                    .setParameter("message", message)
                    .setParameter("type", type)
                    .executeUpdate();
        }

        try {
            InspireLog logEntry = inspireLogRepository.findById(logId).orElseThrow();
            logEntry.setMessage(message);
            logEntry.setType(type);
            inspireLogRepository.save(logEntry);
        } catch (Exception e) {
            logger.severe("Error updating log entry: " + e.getMessage());
            entityManager.getTransaction().rollback();
        } finally {
            entityManager.getTransaction().commit();
        }
    }

    @Transactional
    public void addLogDetails(Long logId, Long spirProjectId, String temp1, String temp2, String temp3, String temp4, String temp5) {
        if (logId == null || logId <= 0) {
            logger.severe("Invalid log ID: " + logId);
            return;
        }

        try {
            entityManager.createNativeQuery("CALL inspire.add_log_details(:logId, :spirProjectId, :temp1, :temp2, :temp3, :temp4, :temp5, @@ROWCOUNT)")
                    .setParameter("logId", logId)
                    .setParameter("spirProjectId", spirProjectId)
                    .setParameter("temp1", temp1)
                    .setParameter("temp2", temp2)
                    .setParameter("temp3", temp3)
                    .setParameter("temp4", temp4)
                    .setParameter("temp5", temp5)
                    .executeUpdate();
        } catch (Exception e) {
            logger.severe("Error adding log details: " + e.getMessage());
            entityManager.getTransaction().rollback();
            // Update Inspire project procedure
            entityManager.createNativeQuery("CALL inspire.update_project_proc(:logId, :message, :type)")
                    .setParameter("logId", logId)
                    .setParameter("message", "Error adding log details")
                    .setParameter("type", "ERROR")
                    .executeUpdate();
        }

        try {
            inspireLogRepository.addLogDetails(logId, spirProjectId, temp1, temp2, temp3, temp4, temp5);
        } catch (Exception e) {
            logger.severe("Error adding log details: " + e.getMessage());
            entityManager.getTransaction().rollback();
        } finally {
            entityManager.getTransaction().commit();
        }
    }
}