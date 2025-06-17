package com.inspire.repository;

import com.inspire.model.InspireSpirRefreshData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InspireSpirRefreshDataRepository extends JpaRepository<InspireSpirRefreshData, Integer> {
    // Find refresh data by project ID (single result)
    InspireSpirRefreshData findByProjectId(Integer projectId);
    
    // Find refresh data by project ID (multiple results)
    List<InspireSpirRefreshData> findAllByProjectId(Integer projectId);
    
    // Update refresh data status to 'E'
    @Modifying
    @Query("UPDATE InspireSpirRefreshData r SET r.status = 'E' WHERE r.projectId = :projectId")
    int updateRefreshDataStatusToE(@Param("projectId") Integer projectId);
    
    // Update refresh data status to specified value
    @Modifying
    @Query("UPDATE InspireSpirRefreshData r SET r.status = :status WHERE r.projectId = :projectId")
    int updateRefreshDataStatus(@Param("projectId") Integer projectId, @Param("status") String status);
}