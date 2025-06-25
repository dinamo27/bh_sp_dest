package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.model.InspireLog;

import java.time.LocalDateTime;

@Repository
public interface InspireLogRepository extends JpaRepository<InspireLog, Long> {
    
    @Modifying
    @Transactional(propagation = Propagation.MANDATORY)
    @Query(value = "INSERT INTO inspire_log (procedure_name, start_time, status, additional_info) VALUES (:procedureName, :startTime, :status, :additionalInfo) RETURNING log_id", nativeQuery = true)
    Long createLogEntry(
        @Param("procedureName") String procedureName,
        @Param("startTime") LocalDateTime startTime,
        @Param("status") String status,
        @Param("additionalInfo") String additionalInfo);
    
    @Modifying
    @Transactional(propagation = Propagation.MANDATORY)
    @Query(value = "UPDATE inspire_log SET status = :status, end_time = :endTime, affected_rows = :affectedRows, additional_info = :additionalInfo WHERE log_id = :logId", nativeQuery = true)
    int updateLogEntry(
        @Param("logId") Long logId,
        @Param("status") String status,
        @Param("endTime") LocalDateTime endTime,
        @Param("affectedRows") Integer affectedRows,
        @Param("additionalInfo") String additionalInfo);
    
    @Modifying
    @Transactional(propagation = Propagation.MANDATORY)
    @Query(value = "INSERT INTO inspire_log_details (log_id, step_name, affected_rows, additional_info) VALUES (:logId, :stepName, :affectedRows, :additionalInfo)", nativeQuery = true)
    int addLogDetail(
        @Param("logId") Long logId,
        @Param("stepName") String stepName,
        @Param("affectedRows") Integer affectedRows,
        @Param("additionalInfo") String additionalInfo);
}