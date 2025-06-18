package com.inspire.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.inspire.model.InspireProject;

@Repository
public interface InspireProjectRepository extends JpaRepository<InspireProject, Integer> {
    
    @Modifying
    @Transactional
    @Query("UPDATE InspireProject p SET p.status = :status WHERE p.projectId = :projectId")
    int updateProjectStatus(Integer projectId, String status);
    
    InspireProject findByProjectId(Integer projectId);
    
    @Query("SELECT p FROM InspireProject p WHERE p.status = :status")
    List<InspireProject> findByStatus(String status);
    
    @Query("SELECT p FROM InspireProject p WHERE p.createdBy = :userId")
    List<InspireProject> findProjectsByUser(Integer userId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM InspireProject p WHERE p.projectId = :projectId")
    void deleteByProjectId(Integer projectId);
}