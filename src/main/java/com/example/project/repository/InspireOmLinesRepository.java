package com.example.project.repository;

import com.example.project.entity.InspireOmLines;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import java.util.List;

public interface InspireOmLinesRepository extends JpaRepository<InspireOmLines, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE InspireOmLines i SET i.processed = 'E' WHERE i.spirProjectId = :spirProjectId AND (i.processed IS NULL OR i.processed = 'P')")
    int updateProcessedBySpirProjectId(@Param("spirProjectId") Long spirProjectId);

    @Query("SELECT i FROM InspireOmLines i WHERE i.spirProjectId = :spirProjectId")
    @Lock(LockModeType.OPTIMISTIC)
    List<InspireOmLines> findInspireOmLinesBySpirProjectId(@Param("spirProjectId") Long spirProjectId);
}