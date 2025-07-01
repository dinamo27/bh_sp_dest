package com.example.project.repository;

import com.example.project.entity.InspireOmLines;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Transactional;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;

@Repository
public interface InspireOmLinesRepository extends JpaRepository<InspireOmLines, Long> {

    @Modifying
    @Query("UPDATE InspireOmLines SET processed = 'E' WHERE spirProjectId = :spirProjectId")
    void updateProcessedStatus(@Param("spirProjectId") Long spirProjectId);

    @Transactional
    @Modifying
    @Query("UPDATE InspireOmLines SET processed = 'E', logId = :logId, temp1 = :temp1, temp2 = :temp2, temp3 = :temp3, temp4 = :temp4, temp5 = :temp5 WHERE spirProjectId = :spirProj AND (processed = 'P' OR processed IS NULL)")
    void updateInspireOmLines(@Param("spirProj") String spirProj, @Param("logId") String logId, @Param("temp1") String temp1, @Param("temp2") String temp2, @Param("temp3") String temp3, @Param("temp4") String temp4, @Param("temp5") String temp5);

    @Transactional
    @Modifying
    @Query("UPDATE InspireOmLines SET processed = 'E' WHERE spirProjectId = :spirProjectId AND processed = 'P'")
    void updateProcessedBySpirProjectId(@Param("spirProjectId") Long spirProjectId);

    @Query("SELECT i FROM InspireOmLines i WHERE i.spirProjectId = :spirProjectId AND i.processed = 'P'")
    List<InspireOmLines> findBySpirProjectIdAndProcessed(@Param("spirProjectId") Long spirProjectId);

    @Transactional
    default void updateInspireOmLinesWithLock(String spirProj, String logId, String temp1, String temp2, String temp3, String temp4, String temp5) {
        List<InspireOmLines> inspireOmLinesList = findBySpirProjectIdAndProcessed(Long.parseLong(spirProj));
        if (!inspireOmLinesList.isEmpty()) {
            for (InspireOmLines inspireOmLines : inspireOmLinesList) {
                try {
                    inspireOmLines.setProcessed("E");
                    inspireOmLines.setLogId(logId);
                    inspireOmLines.setTemp1(temp1);
                    inspireOmLines.setTemp2(temp2);
                    inspireOmLines.setTemp3(temp3);
                    inspireOmLines.setTemp4(temp4);
                    inspireOmLines.setTemp5(temp5);
                    save(inspireOmLines);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}