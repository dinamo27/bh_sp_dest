package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectTechnicalResetRepository extends JpaRepository<Object, Long> {
    
    @Modifying
    @Query("UPDATE Project p SET p.status = 'COMPLETED' WHERE p.projectId = :projectId")
    int updateProjectStatusToCompleted(@Param("projectId") Long projectId);

    @Modifying
    @Query("UPDATE PartsGroupedRecalc p SET p.processed = true WHERE p.projectId = :projectId")
    int markPartsGroupedRecalcAsProcessed(@Param("projectId") Long projectId);

    @Modifying
    @Query("UPDATE SpirRefreshData s SET s.processed = true WHERE s.projectId = :projectId")
    int markSpirRefreshDataAsProcessed(@Param("projectId") Long projectId);

    @Modifying
    @Query("DELETE FROM PositionToActivityMapping p WHERE p.projectId = :projectId")
    int deletePositionToActivityMappings(@Param("projectId") Long projectId);

    @Modifying
    @Query("UPDATE OperationalManagementProject o SET o.processingStatus = 'E', o.message = :errorMessage " +
           "WHERE o.projectId = :projectId AND (o.processingStatus = 'P' OR o.processingStatus IS NULL)")
    int updateOperationalManagementProjectToError(@Param("projectId") Long projectId, @Param("errorMessage") String errorMessage);

    @Modifying
    @Query("UPDATE OperationalManagementLines o SET o.processingStatus = 'E', o.message = :errorMessage " +
           "WHERE o.projectId = :projectId AND (o.processingStatus = 'P' OR o.processingStatus IS NULL)")
    int updateOperationalManagementLinesToError(@Param("projectId") Long projectId, @Param("errorMessage") String errorMessage);
}