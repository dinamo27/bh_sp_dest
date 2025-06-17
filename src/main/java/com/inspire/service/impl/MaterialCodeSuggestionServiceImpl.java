package com.inspire.service.impl;

import com.inspire.repository.InspireLockedCodesReplacedByRepository;
import com.inspire.repository.InspirePartsRepository;
import com.inspire.repository.InspireProjectRepository;
import com.inspire.repository.NpeamLockedCompBaseRepository;
import com.inspire.service.MaterialCodeSuggestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MaterialCodeSuggestionServiceImpl implements MaterialCodeSuggestionService {
    
    private static final Logger logger = LoggerFactory.getLogger(MaterialCodeSuggestionServiceImpl.class);
    private static final String CODE_PREFIX = "inSPIRe suggested from superseded notes: ";
    private static final Pattern MATERIAL_CODE_PATTERN = Pattern.compile("([A-Za-z][0-9]|[0-9][A-Za-z])");
    
    private final InspireProjectRepository inspireProjectRepository;
    private final NpeamLockedCompBaseRepository npeamLockedCompBaseRepository;
    private final InspireLockedCodesReplacedByRepository inspireLockedCodesReplacedByRepository;
    private final InspirePartsRepository inspirePartsRepository;
    
    public MaterialCodeSuggestionServiceImpl(
            InspireProjectRepository inspireProjectRepository,
            NpeamLockedCompBaseRepository npeamLockedCompBaseRepository,
            InspireLockedCodesReplacedByRepository inspireLockedCodesReplacedByRepository,
            InspirePartsRepository inspirePartsRepository) {
        this.inspireProjectRepository = inspireProjectRepository;
        this.npeamLockedCompBaseRepository = npeamLockedCompBaseRepository;
        this.inspireLockedCodesReplacedByRepository = inspireLockedCodesReplacedByRepository;
        this.inspirePartsRepository = inspirePartsRepository;
    }
    
    @Override
    @Transactional
    public void suggestReplacementMaterialCodes(String projectId) {
        Instant startTime = Instant.now();
        logger.info("Starting material code suggestion process for project: {}", projectId);
        
        boolean isProjectInInspireTable = inspireProjectRepository.existsById(projectId);
        logger.info("Project {} exists in inspire_project table: {}", projectId, isProjectInInspireTable);
        
        inspirePartsRepository.resetMarkFieldForProject(projectId);
        logger.info("Reset mark field in inspire_parts table for project: {}", projectId);
        
        int processedCount;
        if (isProjectInInspireTable) {
            processedCount = processStandardMode(projectId);
        } else {
            processedCount = processEamFeedingMode(projectId);
        }
        
        Duration executionTime = Duration.between(startTime, Instant.now());
        logger.info("Material code suggestion process completed for project: {}. Processed {} records in {} ms",
                projectId, processedCount, executionTime.toMillis());
    }
    
    private int processStandardMode(String projectId) {
        logger.info("Processing in standard mode for project: {}", projectId);
        List<Map<String, Object>> records = inspireLockedCodesReplacedByRepository.findRecordsForProcessing(projectId);
        
        int updatedCount = 0;
        for (Map<String, Object> record : records) {
            String id = (String) record.get("id");
            String notes = (String) record.get("notes");
            
            String normalizedText = normalizeText(notes);
            String potentialCode = extractPotentialCode(normalizedText);
            
            if (potentialCode != null) {
                inspireLockedCodesReplacedByRepository.updateSuggestedCode(id, CODE_PREFIX + potentialCode);
                updatedCount++;
            }
        }
        
        logger.info("Standard mode processing completed. Updated {} records", updatedCount);
        return updatedCount;
    }
    
    private int processEamFeedingMode(String projectId) {
        logger.info("Processing in EAM feeding mode for project: {}", projectId);
        List<Map<String, Object>> records = npeamLockedCompBaseRepository.findRecordsForProcessing(projectId);
        
        int updatedCount = 0;
        for (Map<String, Object> record : records) {
            String id = (String) record.get("id");
            String notes = (String) record.get("notes");
            
            String normalizedText = normalizeText(notes);
            String potentialCode = extractPotentialCode(normalizedText);
            
            if (potentialCode != null) {
                npeamLockedCompBaseRepository.updateSuggestedCode(id, CODE_PREFIX + potentialCode);
                updatedCount++;
            }
        }
        
        logger.info("EAM feeding mode processing completed. Updated {} records", updatedCount);
        return updatedCount;
    }
    
    private String normalizeText(String text) {
        if (text == null) {
            return null;
        }
        
        String normalized = text.toLowerCase();
        
        normalized = normalized.replace("acciaio", "steel");
        normalized = normalized.replace("ferro", "iron");
        normalized = normalized.replace("alluminio", "aluminum");
        normalized = normalized.replace("rame", "copper");
        normalized = normalized.replace("ottone", "brass");
        normalized = normalized.replace("bronzo", "bronze");
        
        normalized = normalized.replaceAll("[^a-zA-Z0-9]", " ");
        normalized = normalized.replaceAll("\\s+", " ");
        
        return normalized.trim();
    }
    
    private String extractPotentialCode(String normalizedText) {
        if (normalizedText == null || normalizedText.isEmpty()) {
            return null;
        }
        
        Matcher matcher = MATERIAL_CODE_PATTERN.matcher(normalizedText);
        if (matcher.find()) {
            String potentialCode = matcher.group();
            
            int maxLength = 40;
            if (potentialCode.length() > maxLength) {
                potentialCode = potentialCode.substring(0, maxLength);
            }
            
            return potentialCode;
        }
        
        return null;
    }
}