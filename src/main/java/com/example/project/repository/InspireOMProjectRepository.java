package com.example.project.repository;

import com.example.project.entity.InspireOMProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InspireOMProjectRepository extends JpaRepository<InspireOMProject, Long> {

    @Modifying
    @Query("UPDATE InspireOMProject i SET i.processFlag = 'E', i.errorMessage = 'Forced om callback' WHERE i.spirProjectId = :spirProjectId AND (i.processFlag = 'P' OR i.processFlag IS NULL)")
    void updateOMProject(@Param("spirProjectId") Long spirProjectId);
}