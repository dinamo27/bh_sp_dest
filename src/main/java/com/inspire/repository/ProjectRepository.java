package com.inspire.repository;

import com.inspire.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {

    Optional<Project> findByProjectId(Integer projectId);

    @Modifying
    @Query("UPDATE Project p SET p.status = :status, p.recalcRequired = :recalcRequired, p.refreshRequired = :refreshRequired WHERE p.projectId = :projectId")
    int updateProjectStatusAndFlags(
        @Param("projectId") Integer projectId,
        @Param("status") String status,
        @Param("recalcRequired") Boolean recalcRequired,
        @Param("refreshRequired") Boolean refreshRequired
    );
}