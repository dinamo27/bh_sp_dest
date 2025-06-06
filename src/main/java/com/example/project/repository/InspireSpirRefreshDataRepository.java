package com.example.project.repository;

import com.example.project.entity.InspireSpirRefreshData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public interface InspireSpirRefreshDataRepository extends JpaRepository<InspireSpirRefreshData, Long> {

    @Modifying
    @Query(value = "UPDATE inspire_spir_refresh_data SET processed = 'Y' WHERE spirProjectId = :spirProjectId", nativeQuery = true)
    void updateProcessedBySpirProjectId(@Param("spirProjectId") Long spirProjectId);
}