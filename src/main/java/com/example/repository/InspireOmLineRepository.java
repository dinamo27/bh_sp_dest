package com.example.repository;

import com.example.model.InspireOmLine;
import com.example.model.InspireOmLineId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InspireOmLineRepository extends JpaRepository<InspireOmLine, InspireOmLineId> {
    
    @Modifying
    @Query("UPDATE InspireOmLine l SET l.status = 'E', l.errorMessage = :errorMessage " +
           "WHERE l.id.projectId = :projectId AND l.status = 'P'")
    int markPendingWithError(
        @Param("projectId") String projectId, 
        @Param("errorMessage") String errorMessage
    );
}