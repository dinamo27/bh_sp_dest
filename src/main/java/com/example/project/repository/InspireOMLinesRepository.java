package com.example.project.repository;

import com.example.project.entity.InspireOMLines;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import java.util.List;

@Repository
public interface InspireOMLinesRepository extends JpaRepository<InspireOMLines, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE InspireOMLines o SET o.processed = 'E' WHERE o.spirProjectId = :spirProjectId AND (o.processed = 'P' OR o.processed IS NULL)")
    void updateOMLines(@Param("spirProjectId") Long spirProjectId);

    @Query("SELECT o FROM InspireOMLines o WHERE o.spirProjectId = :spirProjectId AND (o.processed = 'P' OR o.processed IS NULL)")
    List<InspireOMLines> findOMLinesToUpdate(@Param("spirProjectId") Long spirProjectId);

    @Query("SELECT o FROM InspireOMLines o WHERE o.spirProjectId = :spirProjectId = :spirProjectId AND o.processed = 'E'")
    List<InspireOMLines> findOMLinesUpdated(@Param("spirProjectId") Long spirProjectId);

    @Transactional
    @Modifying
    @Query("UPDATE InspireOMLines o SET o.processed = 'E' WHERE o IN (:omLines)")
    void batchUpdateOMLines(@Param("omLines") List<InspireOMLines> omLines);

    @Transactional
    @Modifying
    @Query("UPDATE InspireOMLines o SET o.processed = 'E' WHERE o IN (:omLines)")
    void batchUpdateOMLinesWithOptimisticLocking(@Param("omLines") List<InspireOMLines> omLines);

    default void updateOMLinesWithBatchingAndPagination(Long spirProjectId) {
        int batchSize = 100;
        int currentPage = 0;

        while (true) {
            List<InspireOMLines> omLinesToUpdate = findOMLinesToUpdate(spirProjectId, currentPage, batchSize);

            if (omLinesToUpdate.isEmpty()) {
                break;
            }

            batchUpdateOMLines(omLinesToUpdate);

            currentPage++;
        }
    }

    @Query("SELECT o FROM InspireOMLines o WHERE o.spirProjectId = :spirProjectId AND (o.processed = 'P' OR o.processed IS NULL)")
    List<InspireOMLines> findOMLinesToUpdate(@Param("spirProjectId") Long spirProjectId, @Param("currentPage") int currentPage, @Param("batchSize") int batchSize);

    default void updateOMLinesWithOptimisticLocking(Long spirProjectId) {
        List<InspireOMLines> omLinesToUpdate = findOMLinesToUpdate(spirProjectId);

        for (InspireOMLines omLine : omLinesToUpdate) {
            try {
                InspireOMLines omLineWithLock = findById(omLine.getId()).orElseThrow();
                lock(omLineWithLock, LockModeType.OPTIMISTIC);

                if (omLineWithLock.getProcessed().equals("P") || omLineWithLock.getProcessed() == null) {
                    omLineWithLock.setProcessed("E");
                    save(omLineWithLock);
                }
            } catch (Exception e) {
                // handle exception
            }
        }
    }

    default void lock(InspireOMLines omLine, LockModeType lockModeType) {
        // implement locking logic
    }
}