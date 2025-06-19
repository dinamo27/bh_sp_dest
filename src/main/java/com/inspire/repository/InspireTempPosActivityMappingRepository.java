package com.inspire.repository;

import com.inspire.model.InspireTempPosActivityMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InspireTempPosActivityMappingRepository extends JpaRepository<InspireTempPosActivityMapping, String> {
    
    @Modifying
    @Query("DELETE FROM InspireTempPosActivityMapping m WHERE m.projectId = :projectId")
    int deleteByProjectId(@Param("projectId") String projectId);
}