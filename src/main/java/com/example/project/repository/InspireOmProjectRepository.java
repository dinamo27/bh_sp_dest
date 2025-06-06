package com.example.project.repository;

import com.example.project.entity.InspireOmProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface InspireOmProjectRepository extends JpaRepository<InspireOmProject, Long> {

    @Transactional
    @Query("UPDATE InspireOmProject SET processFlag = :processFlag, errorMessage = :errorMessage WHERE spirProjectId = :spirProjectId AND (processFlag = 'P' OR processFlag IS NULL)")
    int updateOmProject(@Param("spirProjectId") Long spirProjectId, @Param("processFlag") String processFlag, @Param("errorMessage") String errorMessage);
}