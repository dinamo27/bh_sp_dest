package com.example.project.repository.dao;

import com.example.project.entity.InspireOMProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import java.util.List;

@Repository
public interface InspireOMProjectRepository extends JpaRepository<InspireOMProject, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE InspireOMProject SET processFlag = :processFlag WHERE spirProjectId = :spirProjectId")
    int updateProcessFlagBySpirProjectId(@Param("spirProjectId") Long spirProjectId, @Param("processFlag") String processFlag);

    @Modifying
    @Transactional
    @Query("UPDATE InspireOMProject SET errorMessage = :errorMessage WHERE spirProjectId = :spirProjectId")
    int updateErrorMessageBySpirProjectId(@Param("spirProjectId") Long spirProjectId, @Param("errorMessage") String errorMessage);

    @Modifying
    @Transactional
    @Query("UPDATE InspireOMProject SET processFlag = 'E', errorMessage = 'Forced om callback' WHERE spirProjectId = :spirProjectId")
    @Lock(LockModeType.OPTIMISTIC)
    void updateOMProject(@Param("spirProjectId") Long spirProjectId) throws Exception;
}