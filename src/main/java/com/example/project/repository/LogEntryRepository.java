package com.example.project.repository;

import com.example.project.entity.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE log_entry SET log_message = :logMessage WHERE log_id = :logId", nativeQuery = true)
    void updateLogEntry(@Param("logId") Long logId, @Param("logMessage") String logMessage) throws EntityNotFoundException;

    @Modifying
    @Transactional
    @Query(value = "UPDATE log_entry SET status = :status, message = :message WHERE log_id = :logId", nativeQuery = true)
    void updateLogEntry(@Param("logId") Long logId, @Param("status") String status, @Param("message") String message) throws EntityNotFoundException;
}