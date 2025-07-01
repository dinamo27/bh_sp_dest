package com.example.project.repository;

import com.example.project.entity.InspireSpirRefreshData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Transactional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Optional;

@Repository
public interface InspireSpirRefreshDataRepository extends JpaRepository<InspireSpirRefreshData, Long> {

    @Modifying
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    @Query("UPDATE InspireSpirRefreshData SET processed = 'Y' WHERE spirProjectId = :spirProjectId")
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "10000")})
    @Lock(LockModeType.OPTIMISTIC)
    void updateProcessedBySpirProjectId(@Param("spirProjectId") Long spirProjectId);
}