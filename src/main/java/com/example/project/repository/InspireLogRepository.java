package com.example.project.repository;

import com.example.project.entity.InspireLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import java.util.List;

@Repository
public interface InspireLogRepository extends JpaRepository<InspireLog, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE InspireLog il SET il.message = :message, il.type = :type WHERE il.logId = :logId")
    @Lock(LockModeType.OPTIMISTIC)
    void updateLogEntry(@Param("logId") Long logId, @Param("message") String message, @Param("type") String type);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO inspire_log_details (log_id, spir_project_id, temp1, temp2, temp3, temp4, temp5) VALUES (:logId, :spirProjectId, :temp1, :temp2, :temp3, :temp4, :temp5)", nativeQuery = true)
    void addLogDetails(@Param("logId") Long logId, @Param("spirProjectId") Long spirProjectId, @Param("temp1") String temp1, @Param("temp2") String temp2, @Param("temp3") String temp3, @Param("temp4") String temp4, @Param("temp5") String temp5);

    @Query("SELECT il FROM InspireLog il WHERE il.logId = :logId")
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
    @Lock(LockModeType.OPTIMISTIC)
    InspireLog findLogEntryForUpdate(@Param("logId") Long logId);
}