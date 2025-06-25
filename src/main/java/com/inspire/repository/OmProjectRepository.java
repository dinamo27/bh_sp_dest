package com.inspire.repository;

import com.inspire.model.OmProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OmProjectRepository extends JpaRepository<OmProject, Long> {
    
    List<OmProject> findByProjectIdAndProcessedFlag(Integer projectId, Boolean processedFlag);
    
    @Modifying
    @Query("UPDATE OmProject o SET o.errorFlag = true, o.errorMessage = CONCAT(COALESCE(o.errorMessage, ''), ' ', :errorMessage) " +
           "WHERE o.projectId = :projectId AND o.processedFlag = false")
    int forceErrorStateByProjectId(@Param("projectId") Long projectId, @Param("errorMessage") String errorMessage);
    
    @Modifying
    @Query("UPDATE OmProject o SET o.errorFlag = true, o.errorMessage = CONCAT(COALESCE(o.errorMessage, ''), ' Forced om callback') WHERE o.projectId = :projectId AND o.processedFlag = false")
    int forceErrorOnUnprocessedRecords(@Param("projectId") Integer projectId);
}