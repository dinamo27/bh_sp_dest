package com.inspire.repository;

import com.inspire.model.InspireTempPosActivityMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InspireTempPosActivityMappingRepository extends JpaRepository<InspireTempPosActivityMapping, String> {
    
    List<InspireTempPosActivityMapping> findByProjectId(String projectId);
    
    @Modifying
    @Query("DELETE FROM InspireTempPosActivityMapping i WHERE i.projectId = :projectId")
    int deleteByProjectId(@Param("projectId") String projectId);
}