package com.example.project.service;

import com.example.project.repository.InspireOmLinesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.logging.Logger;

@Service
@Slf4j
@RequiredArgsConstructor
public class InspireOmLinesService {

    private final InspireOmLinesRepository inspireOmLinesRepository;
    private final TransactionTemplate transactionTemplate;
    private final Logger logger = Logger.getLogger(InspireOmLinesService.class.getName());

    @Transactional(propagation = Propagation.REQUIRED)
    public int updateInspireOmLines(Long spirProj, Long logId, String temp1, String temp2, String temp3, String temp4, String temp5) {
        if (spirProj == null || logId == null || temp1 == null || temp2 == null || temp3 == null || temp4 == null || temp5 == null) {
            throw new RuntimeException("Input parameters cannot be null");
        }

        inspireOmLinesRepository.updateProcessedStatus(spirProj);

        int successFlag = 0;

        try {
            transactionTemplate.execute(status -> {
                logger.info("COMPLETED MESSAGE");
                logger.info("Log details: logId={}, spirProj={}, temp1={}, temp2={}, temp3={}, temp4={}, temp5={}", logId, spirProj, temp1, temp2, temp3, temp4, temp5);
                return null;
            });
            successFlag = 0;
        } catch (Exception e) {
            transactionTemplate.execute(status -> {
                logger.severe("ERROR MESSAGE: " + e.getMessage());
                logger.severe("Inspire project proc update: spirProj=" + spirProj);
                return null;
            });
            successFlag = -1;
        } finally {
            transactionTemplate.execute(status -> {
                status.flush();
                return null;
            });
        }

        return successFlag;
    }
}