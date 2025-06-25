package com.inspire.repository;

import com.inspire.model.TempPosToActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TempPosToActivityRepository extends JpaRepository<TempPosToActivity, Long> {
    
    List<TempPosToActivity> findByProjectId(Integer projectId);
    
    @Modifying
    @Query("DELETE FROM TempPosToActivity t WHERE t.projectId = :projectId")
    int deleteByProjectId(@Param("projectId") Integer projectId);
}