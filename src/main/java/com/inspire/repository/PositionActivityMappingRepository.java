package com.inspire.repository;

import com.inspire.model.PositionActivityMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PositionActivityMappingRepository extends JpaRepository<PositionActivityMapping, Integer> {
    
    List<PositionActivityMapping> findByProjectId(Integer projectId);

    @Modifying
    @Query("DELETE FROM PositionActivityMapping m WHERE m.projectId = :projectId")
    int deleteByProjectId(@Param("projectId") Integer projectId);
}