package com.inspire.repository;

import com.inspire.model.InspireOmLines;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InspireOmLinesRepository extends JpaRepository<InspireOmLines, Integer> {
    
    List<InspireOmLines> findByProjectId(Integer projectId);
    
    @Modifying
    @Query("UPDATE InspireOmLines l SET l.status = 'E' WHERE l.projectId = :projectId AND (l.status = 'P' OR l.processedFlag != 'Y')")
    int updatePendingOrUnprocessedToError(@Param("projectId") Integer projectId);
}