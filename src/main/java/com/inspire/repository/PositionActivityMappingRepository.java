package com.inspire.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.inspire.model.PositionActivityMapping;

@Repository
public interface PositionActivityMappingRepository extends JpaRepository<PositionActivityMapping, Long> {
    
    @Modifying
    @Transactional
    @Query("DELETE FROM PositionActivityMapping p WHERE p.projectId = :projectId")
    int deleteByProjectId(Integer projectId);
}