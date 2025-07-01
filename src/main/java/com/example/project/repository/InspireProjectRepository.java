package com.example.project.repository;

import com.example.project.entity.InspireProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.logging.Logger;

@Repository
public class InspireProjectRepository implements JpaRepository<InspireProject, Long> {

    private static final Logger LOGGER = Logger.getLogger(InspireProjectRepository.class.getName());

    private final EntityManager entityManager;

    public InspireProjectRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public void updateSpirStatusBySpirProjectId(Long spirProjectId) {
        if (spirProjectId == null) {
            throw new IllegalArgumentException("spirProjectId cannot be null");
        }

        String query = "UPDATE inspire_project SET spir_status = 'COMPLETED' WHERE spir_project_id = :spirProjectId";
        try {
            entityManager.createQuery(query).setParameter("spirProjectId", spirProjectId).executeUpdate();
        } catch (Exception e) {
            LOGGER.severe("Error updating spir status: " + e.getMessage());
            throw new RuntimeException("Error updating spir status", e);
        }
    }
}