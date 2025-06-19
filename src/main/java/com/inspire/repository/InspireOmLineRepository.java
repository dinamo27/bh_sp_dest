package com.inspire.repository;

import com.inspire.model.InspireOmLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InspireOmLineRepository extends JpaRepository<InspireOmLine, String> {
    
    @Modifying
    @Query("UPDATE InspireOmLine i SET i.status = 'E', i.errorMessage = :errorMessage WHERE i.projectId = :projectId AND i.status = 'P'")
    int updatePendingLinesWithError(
        @Param("projectId") String projectId,
        @Param("errorMessage") String errorMessage
    );
    
    List<InspireOmLine> findByProjectIdAndStatus(String projectId, char status);
}