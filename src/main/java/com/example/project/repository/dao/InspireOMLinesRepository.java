package com.example.project.repository.dao;

import com.example.project.entity.InspireOMLines;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface InspireOMLinesRepository extends JpaRepository<InspireOMLines, Long> {

    @Transactional
    @Modifying
    @Query("update InspireOMLines set processed = 'E' where spirProjectId = :spirProjectId")
    void updateOMLines(@Param("spirProjectId") Long spirProjectId);
}