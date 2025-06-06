package com.example.project.repository.dao;

import com.example.project.entity.InspireProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.LockModeType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.QueryHint;
import java.util.List;

@Repository
public interface InspireProjectRepository extends JpaRepository<InspireProject, Long> {

    @Modifying
    @Query("UPDATE InspireProject SET spirStatus = 'COMPLETED' WHERE spirProjectId = :spirProjectId")
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
    @Lock(LockModeType.OPTIMISTIC)
    @Transactional
    void updateProjectStatus(@Param("spirProjectId") Long spirProjectId);
}