package com.inspire.repository;

import com.inspire.model.InspireSpirRefreshData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InspireSpirRefreshDataRepository extends JpaRepository<InspireSpirRefreshData, Integer> {
    
    List<InspireSpirRefreshData> findByProjectId(Integer projectId);
    
    @Modifying
    @Query("UPDATE InspireSpirRefreshData s SET s.processedFlag = 'Y' WHERE s.projectId = :projectId")
    int markAsProcessed(@Param("projectId") Integer projectId);
}