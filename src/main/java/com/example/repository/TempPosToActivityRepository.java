package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.entity.TempPosToActivity;

@Repository
public interface TempPosToActivityRepository extends JpaRepository<TempPosToActivity, Long> {
    
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM inspire_temp_pos_to_activity WHERE project_id = :projectId", nativeQuery = true)
    int deleteAllByProjectId(@Param("projectId") Long projectId);
}