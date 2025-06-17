package com.example.service.impl;

import com.example.repository.MaterialCodeSuggestionRepository;
import com.example.service.MaterialCodeSuggestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class MaterialCodeSuggestionServiceImpl implements MaterialCodeSuggestionService {
    private static final Logger logger = LoggerFactory.getLogger(MaterialCodeSuggestionServiceImpl.class);
    
    private final MaterialCodeSuggestionRepository materialCodeSuggestionRepository;
    
    @Autowired
    public MaterialCodeSuggestionServiceImpl(MaterialCodeSuggestionRepository materialCodeSuggestionRepository) {
        this.materialCodeSuggestionRepository = materialCodeSuggestionRepository;
    }
    
    @Override
    @Transactional
    public int identifyAndSuggestReplacementMaterialCodes(String projectId) throws Exception {
        Instant startTime = Instant.now();
        int affectedRowsCount = 0;
        
        logger.info("Starting material code suggestion process for project: {}", projectId);
        
        try {
            boolean isProjectInInspire = materialCodeSuggestionRepository.isProjectInInspire(projectId);
            
            materialCodeSuggestionRepository.resetMarkFieldInInspireParts(projectId);
            
            if (isProjectInInspire) {
                logger.info("Processing in Standard Mode for project: {}", projectId);
                
                List<Map<String, Object>> recordsToProcess = 
                    materialCodeSuggestionRepository.getInspireLockedCodesForProcessing(projectId);
                
                logger.info("Found {} records to process in inspire_locked_codes_replaced_by", recordsToProcess.size());
                
                for (Map<String, Object> record : recordsToProcess) {
                    Long id = ((Number) record.get("id")).longValue();
                    String supersededNotes = (String) record.get("superseded_notes");
                    
                    String materialCode = materialCodeSuggestionRepository.processMaterialCodeFromText(supersededNotes);
                    
                    if (materialCode != null) {
                        int updated = materialCodeSuggestionRepository.updateInspireLockedCodesReplacedBy(id, materialCode);
                        affectedRowsCount += updated;
                    }
                }
            } else {
                logger.info("Processing in EAM Feeding Mode for project: {}", projectId);
                
                List<Map<String, Object>> recordsToProcess = 
                    materialCodeSuggestionRepository.getNpeamLockedCompBaseForProcessing(projectId);
                
                logger.info("Found {} records to process in npeam_locked_comp_base", recordsToProcess.size());
                
                for (Map<String, Object> record : recordsToProcess) {
                    Long id = ((Number) record.get("id")).longValue();
                    String supersededNotes = (String) record.get("superseded_notes");
                    
                    String materialCode = materialCodeSuggestionRepository.processMaterialCodeFromText(supersededNotes);
                    
                    if (materialCode != null) {
                        int updated = materialCodeSuggestionRepository.updateNpeamLockedCompBaseSuggestedMaterialCode(id, materialCode);
                        affectedRowsCount += updated;
                    }
                }
            }
            
            Instant endTime = Instant.now();
            Duration executionTime = Duration.between(startTime, endTime);
            
            logger.info("Material code suggestion process completed successfully for project: {}", projectId);
            logger.info("Affected rows: {}", affectedRowsCount);
            logger.info("Execution time: {} seconds", executionTime.getSeconds());
            
            return affectedRowsCount;
            
        } catch (Exception e) {
            logger.error("Error in material code suggestion process for project {}: {}", projectId, e.getMessage(), e);
            throw new Exception("Failed to process material code suggestions: " + e.getMessage(), e);
        }
    }
}