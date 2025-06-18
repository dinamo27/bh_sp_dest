package com.inspire.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.inspire.model.InspireOmProject;

@Repository
public interface InspireOmProjectRepository extends JpaRepository<InspireOmProject, Integer> {
    
    @Modifying
    @Transactional
    @Query("UPDATE InspireOmProject p SET p.status = :newStatus WHERE p.projectId = :projectId AND p.status = :currentStatus")
    int updateStatusForPendingRecords(Integer projectId, Character currentStatus, Character newStatus);
}