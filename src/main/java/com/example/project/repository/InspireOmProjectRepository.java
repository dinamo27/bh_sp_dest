package com.example.project.repository;

import com.example.project.entity.InspireOmProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.jpa.repository.Transactional;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;

@Repository
public interface InspireOmProjectRepository extends JpaRepository<InspireOmProject, Long> {

    @Query("SELECT p FROM InspireOmProject p WHERE p.spirProjectId = :spirProjectId")
    InspireOmProject findInspireOmProjectBySpirProjectId(Long spirProjectId);

    @Transactional
    @Modifying
    @Query("UPDATE InspireOmProject p SET p.processFlag = :processFlag WHERE p.spirProjectId = :spirProjectId")
    @QueryHints({@QueryHint(name = "javax.persistence.query.timeout", value = "1000")})
    int updateProcessFlagBySpirProjectId(Long spirProjectId, String processFlag);

    @Transactional
    @Modifying
    @Query("UPDATE InspireOmProject p SET p.errorMessage = :errorMessage WHERE p.spirProjectId = :spirProjectId")
    @QueryHints({@QueryHint(name = "javax.persistence.query.timeout", value = "1000")})
    int updateErrorMessageBySpirProjectId(Long spirProjectId, String errorMessage);

    @Transactional
    @Modifying
    @Query("UPDATE InspireOmProject p SET p.projectStatus = :status WHERE p.spirProjectId = :spirProjectId")
    @QueryHints({@QueryHint(name = "javax.persistence.query.timeout", value = "1000")})
    int updateProjectStatus(Long spirProjectId, String status);

    @Transactional
    @Modifying
    @Query("UPDATE InspireOmProject p SET p.omProject = :omProject WHERE p.spirProjectId = :spirProjectId")
    @QueryHints({@QueryHint(name = "javax.persistence.query.timeout", value = "1000")})
    int updateOmProject(Long spirProjectId, String omProject);

    @Transactional
    @Modifying
    @Query("UPDATE InspireOmProject p SET p.omLines = :omLines WHERE p.spirProjectId = :spirProjectId")
    @QueryHints({@QueryHint(name = "javax.persistence.query.timeout", value = "1000")})
    int updateOmLines(Long spirProjectId, String omLines);

    @Transactional
    @Modifying
    @Query("UPDATE InspireOmProject p SET p.logEntry.message = :message, p.logEntry.type = :type WHERE p.logEntry.logId = :logId")
    @QueryHints({@QueryHint(name = "javax.persistence.query.timeout", value = "1000")})
    int updateLogEntry(Long logId, String message, String type);
}