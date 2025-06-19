package com.inspire.repository;

import com.inspire.model.InspireSpirRefresh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InspireSpirRefreshRepository extends JpaRepository<InspireSpirRefresh, String> {
    
    @Modifying
    @Query("UPDATE InspireSpirRefresh i SET i.processedFlag = 'Y' WHERE i.projectId = :projectId AND i.processedFlag != 'Y'")
    int updateProcessedFlagByProjectId(@Param("projectId") String projectId);
    
    List<InspireSpirRefresh> findByProjectIdAndProcessedFlagNot(String projectId, char processedFlag);
}