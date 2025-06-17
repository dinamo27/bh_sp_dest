package com.inspire.service.impl;

import com.inspire.model.InspireLockedCodesReplacedBy;
import com.inspire.model.NpeamLockedCompBase;
import com.inspire.repository.InspireLockedCodesReplacedByRepository;
import com.inspire.repository.InspirePartsRepository;
import com.inspire.repository.InspireProjectRepository;
import com.inspire.repository.NpeamLockedCompBaseRepository;
import com.inspire.service.MaterialCodeSuggestionService;
import com.inspire.service.dto.MaterialCodeSuggestionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MaterialCodeSuggestionServiceImpl implements MaterialCodeSuggestionService {

    @Autowired
    private InspireProjectRepository inspireProjectRepository;
    
    @Autowired
    private NpeamLockedCompBaseRepository npeamLockedCompBaseRepository;
    
    @Autowired
    private InspireLockedCodesReplacedByRepository inspireLockedCodesReplacedByRepository;
    
    @Autowired
    private InspirePartsRepository inspirePartsRepository;
    
    private static final Logger logger = LoggerFactory.getLogger(MaterialCodeSuggestionServiceImpl.class);
    
    @Override
    @Transactional
    public MaterialCodeSuggestionResult processMaterialCodeSuggestions(String projectId) {
        logger.info("Starting material code suggestion process for project: {}", projectId);
        long startTime = System.currentTimeMillis();
        int affectedRows = 0;
        
        try {
            boolean projectExists = inspireProjectRepository.existsByProjectId(projectId);
            
            inspirePartsRepository.resetMarkField(projectId);
            
            if (projectExists) {
                logger.info("Processing in Standard Mode for project: {}", projectId);
                affectedRows = processStandardMode(projectId);
            } else {
                logger.info("Processing in EAM Feeding Mode for project: {}", projectId);
                affectedRows = processEamFeedingMode(projectId);
            }
            
            long executionTime = System.currentTimeMillis() - startTime;
            logger.info("Material code suggestion process completed for project: {}. Affected rows: {}, Execution time: {} ms", 
                        projectId, affectedRows, executionTime);
            
            return new MaterialCodeSuggestionResult(affectedRows, executionTime);
        } catch (Exception e) {
            logger.error("Error processing material code suggestions for project: {}", projectId, e);
            throw new RuntimeException("Failed to process material code suggestions", e);
        }
    }
    
    private int processStandardMode(String projectId) {
        List<InspireLockedCodesReplacedBy> records = inspireLockedCodesReplacedByRepository
            .findByProjectIdAndReplacedByIsNullAndSupersededNotesIsNotNull(projectId);
        
        logger.info("Found {} records to process in Standard Mode", records.size());
        
        int count = 0;
        for (InspireLockedCodesReplacedBy record : records) {
            if (processStandardModeRecord(record)) {
                count++;
            }
        }
        
        logger.info("Updated {} records in Standard Mode", count);
        return count;
    }
    
    private int processEamFeedingMode(String projectId) {
        List<NpeamLockedCompBase> records = npeamLockedCompBaseRepository
            .findByProjectIdAndSuggestedMaterialCodeIsNullAndSupersededNotesIsNotNull(projectId);
        
        logger.info("Found {} records to process in EAM Feeding Mode", records.size());
        
        int count = 0;
        for (NpeamLockedCompBase record : records) {
            if (processEamFeedingModeRecord(record)) {
                count++;
            }
        }
        
        logger.info("Updated {} records in EAM Feeding Mode", count);
        return count;
    }
    
    private boolean processStandardModeRecord(InspireLockedCodesReplacedBy record) {
        String supersededNotes = record.getSupersededNotes();
        String normalizedText = normalizeText(supersededNotes);
        String potentialCode = extractPotentialCode(normalizedText);
        
        if (potentialCode != null) {
            String prefixedCode = "SUGGESTED_" + potentialCode;
            record.setReplacedBy(prefixedCode);
            record.setVerificationFlag(true);
            inspireLockedCodesReplacedByRepository.save(record);
            return true;
        }
        return false;
    }
    
    private boolean processEamFeedingModeRecord(NpeamLockedCompBase record) {
        String supersededNotes = record.getSupersededNotes();
        String normalizedText = normalizeText(supersededNotes);
        String potentialCode = extractPotentialCode(normalizedText);
        
        if (potentialCode != null) {
            String prefixedCode = "SUGGESTED_" + potentialCode;
            record.setSuggestedMaterialCode(prefixedCode);
            record.setVerificationFlag(true);
            npeamLockedCompBaseRepository.save(record);
            return true;
        }
        return false;
    }
    
    private String normalizeText(String text) {
        if (text == null) return null;
        
        text = text.replace("VALVOLE", "VAL VOLE")
                  .replace("DISTINTA", "DIS TINTA");
        
        text = text.replaceAll("[^a-zA-Z0-9]", " ");
        
        text = text.replaceAll("\\s+", " ").trim();
        
        return text;
    }
    
    private String extractPotentialCode(String normalizedText) {
        if (normalizedText == null) return null;
        
        Pattern pattern = Pattern.compile("[a-zA-Z]+[0-9]+|[0-9]+[a-zA-Z]+");
        Matcher matcher = pattern.matcher(normalizedText);
        
        if (matcher.find()) {
            String code = matcher.group();
            return code.length() > 40 ? code.substring(0, 40) : code;
        }
        
        return null;
    }
}