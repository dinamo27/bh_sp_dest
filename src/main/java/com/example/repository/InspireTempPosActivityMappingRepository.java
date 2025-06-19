package com.example.repository;

import com.example.model.InspireTempPosActivityMapping;
import com.example.model.InspireTempPosActivityMappingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InspireTempPosActivityMappingRepository extends JpaRepository<InspireTempPosActivityMapping, InspireTempPosActivityMappingId> {
    
    @Modifying
    @Query("DELETE FROM InspireTempPosActivityMapping m WHERE m.id.projectId = :projectId")
    int deleteByProjectId(@Param("projectId") String projectId);
}