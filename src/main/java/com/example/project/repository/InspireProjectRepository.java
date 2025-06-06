package com.example.project.repository;

import com.example.project.entity.InspireOmLine;
import com.example.project.entity.InspireOmProject;
import com.example.project.entity.InspireProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public interface InspireProjectRepository extends JpaRepository<InspireProject, Long> {

    @Modifying
    @Query("UPDATE InspireProject ip SET ip.spirStatus = :spirStatus WHERE ip.spirProjectId = :spirProjectId")
    int updateProjectStatus(Long spirProjectId, String spirStatus);

    @Modifying
    @Query("UPDATE InspireOmProject iop SET iop.processFlag = :processFlag, iop.errorMessage = :errorMessage WHERE iop.spirProjectId = :spirProjectId")
    int updateOmProject(Long spirProjectId, String processFlag, String errorMessage);

    @Modifying
    @Query("UPDATE InspireOmLine iol SET iol.processed = :processed WHERE iol.spirProjectId = :spirProjectId")
    int updateOmLine(Long spirProjectId, String processed);
}