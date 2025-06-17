package com.inspire.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.inspire.entity.InspireParts;

@Repository
public interface InspirePartsRepository extends JpaRepository<InspireParts, Long> {
    
    @Modifying
    @Query("UPDATE InspireParts p SET p.mark = NULL WHERE p.projectId = :projectId")
    int resetMarkByProjectId(@Param("projectId") String projectId);
}