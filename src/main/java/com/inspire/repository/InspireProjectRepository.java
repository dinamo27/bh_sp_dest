package com.inspire.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface InspireProjectRepository extends JpaRepository<InspireProject, Long> {
    
    Optional<InspireProject> findByProjectId(Long projectId);
    
    List<InspireProject> findByStatus(String status);
    
    List<InspireProject> findByCreatedBy(String createdBy);
    
    List<InspireProject> findByNameContainingIgnoreCase(String name);
    
    boolean existsByProjectId(Long projectId);
    
    @Transactional
    @Modifying
    @Query("UPDATE InspireProject p SET p.status = :status WHERE p.projectId = :projectId")
    int updateProjectStatus(@Param("projectId") Long projectId, @Param("status") String status);
    
    @Query("SELECT p FROM InspireProject p WHERE p.createdDate >= :startDate")
    List<InspireProject> findProjectsCreatedAfter(@Param("startDate") java.util.Date startDate);
    
    @Query("SELECT p FROM InspireProject p ORDER BY p.createdDate DESC")
    List<InspireProject> findAllOrderByCreatedDateDesc();
    
    @Transactional
    @Modifying
    @Query("UPDATE InspireProject p SET p.name = :name, p.description = :description WHERE p.projectId = :projectId")
    int updateProjectDetails(@Param("projectId") Long projectId, @Param("name") String name, @Param("description") String description);
}