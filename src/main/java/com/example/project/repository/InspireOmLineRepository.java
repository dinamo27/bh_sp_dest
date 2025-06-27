package com.example.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.project.entity.InspireOmLine;

@Repository
public interface InspireOmLineRepository extends JpaRepository<InspireOmLine, Long> {
    
    @Modifying
    @Query("UPDATE InspireOmLine i SET i.processed = 'E' WHERE i.spirProjectId = :spirProjectId AND (i.processed IS NULL OR i.processed = 'P')")
    int updateProcessedStatusForUnprocessedRecordsByProjectId(@Param("spirProjectId") Long spirProjectId);
}