package com.inspire.repository;

import com.inspire.model.InspireSpirRefresh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InspireSpirRefreshRepository extends JpaRepository<InspireSpirRefresh, String> {
    
    @Modifying
    @Query("UPDATE InspireSpirRefresh r SET r.processedFlag = :flag WHERE r.projectId = :projectId AND r.processedFlag != :flag")
    int updateProcessedFlagByProjectId(@Param("projectId") String projectId, @Param("flag") String flag);
    
    @Modifying
    @Query("UPDATE InspireSpirRefresh r SET r.processedFlag = 'Y' WHERE r.projectId = :projectId AND r.processedFlag != 'Y'")
    int markAsProcessed(@Param("projectId") String projectId);
}