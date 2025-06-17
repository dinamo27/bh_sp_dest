package com.inspire.repository;

import com.inspire.entity.InspireParts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface InspirePartsRepository extends JpaRepository<InspireParts, Long> {
    
    @Modifying
    @Transactional
    @Query("UPDATE InspireParts SET mark = NULL WHERE projectId = :projectId")
    int resetMarkByProjectId(@Param("projectId") String projectId);
}