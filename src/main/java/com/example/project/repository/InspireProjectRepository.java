package com.example.project.repository;

import com.example.project.entity.InspireProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.QueryHint;

@Repository
public interface InspireProjectRepository extends JpaRepository<InspireProject, Long> {

    @Modifying
    @Query(value = "UPDATE inspire_project SET spir_status = 'COMPLETED' WHERE spir_project_id = :spirProjectId", nativeQuery = true)
    @QueryHints({@QueryHint(name = "javax.persistence.query.timeout", value = "3000")})
    @Transactional
    void updateProjectStatus(Long spirProjectId);
}