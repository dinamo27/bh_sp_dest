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
    @Query("UPDATE InspireOmLine o SET o.processed = 'E' WHERE o.spirProjectId = :spirProjectId AND (o.processed IS NULL OR o.processed = 'P')")
    int updateUnprocessedOmLinesToError(@Param("spirProjectId") String spirProjectId);
    
    @Modifying
    @Query("UPDATE InspireOmLine o SET o.processed = 'E', o.errorMessage = :errorMessage WHERE o.spirProjectId = :spirProjectId AND (o.processed IS NULL OR o.processed = 'P')")
    int updateUnprocessedOmLinesToError(@Param("spirProjectId") Long spirProjectId, @Param("errorMessage") String errorMessage);
}