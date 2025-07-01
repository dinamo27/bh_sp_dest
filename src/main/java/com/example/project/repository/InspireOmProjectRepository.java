package com.example.project.repository;

import com.example.project.entity.InspireOmProject;
import com.example.project.entity.InspireOmLines;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InspireOmProjectRepository extends JpaRepository<InspireOmProject, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE InspireOmProject i SET i.processFlag = :processFlag, i.errorMessage = :errorMessage WHERE i.spirProjectId = :spirProjectId")
    void updateProcessFlagAndErrorMessage(@Param("spirProjectId") Long spirProjectId, @Param("processFlag") String processFlag, @Param("errorMessage") String errorMessage);

    @Transactional
    @Modifying
    @Query("UPDATE InspireOmLines l SET l.processed = 'E' WHERE l.spirProjectId = :spirProjectId AND l.processed IN ('P', NULL)")
    void updateProcessedStatus(@Param("spirProjectId") Long spirProjectId);
}