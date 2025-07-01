package com.example.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public interface InspirePositionToActivityTempRepository extends JpaRepository<InspirePositionToActivityTemp, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM InspirePositionToActivityTemp i WHERE i.spirProjectId = :spirProjectId")
    void deleteBySpirProjectId(@Param("spirProjectId") Long spirProjectId);
}