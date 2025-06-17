package com.inspire.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inspire.repository.InspireProjectRepository;
import com.inspire.repository.NpeamLockedCompBaseRepository;
import com.inspire.repository.InspireLockedCodesReplacedByRepository;
import com.inspire.repository.InspirePartsRepository;
import com.inspire.entity.InspireProject;
import com.inspire.entity.NpeamLockedCompBase;
import com.inspire.entity.InspireLockedCodesReplacedBy;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MaterialCodeSuggestionService {
    private static final Logger logger = LoggerFactory.getLogger(MaterialCodeSuggestionService.class);
    private static final String CODE_PREFIX = "inSPIRe suggested from superseded notes: ";
    private static final int MAX_CODE_LENGTH = 40;
    
    private final InspireProjectRepository inspireProjectRepository;
    private final NpeamLockedCompBaseRepository npeamLockedCompBaseRepository;
    private final InspireLockedCodesReplacedByRepository inspireLockedCodesReplacedByRepository;
    private final InspirePartsRepository inspirePartsRepository;
    
    @Autowired
    public MaterialCodeSuggestionService(
            InspireProjectRepository inspireProjectRepository,
            NpeamLockedCompBaseRepository npeamLockedCompBaseRepository,
            InspireLockedCodesReplacedByRepository inspireLockedCodesReplacedByRepository,
            InspirePartsRepository inspirePartsRepository) {
        this.inspireProjectRepository = inspireProjectRepository;
        this.npeamLockedCompBaseRepository = npeamLockedCompBaseRepository;
        this.inspireLockedCodesReplacedByRepository = inspireLockedCodesReplacedByRepository;
        this.inspirePartsRepository = inspirePartsRepository;
    }
    
    @Transactional
    public void suggestReplacementMaterialCodes(String projectId) {
        logger.info("Starting material code suggestion process for project: {}", projectId);
        
        InspireProject project = inspireProjectRepository.findByProjectId(projectId);
        boolean isEamFeedingMode = (project != null);
        
        inspirePartsRepository.resetMarkFieldForProject(projectId);
        
        int processedCount;
        if (isEamFeedingMode) {
            logger.info("Processing in EAM feeding mode for project: {}", projectId);
            processedCount = processEamFeedingMode(projectId);
        } else {
            logger.info("Processing in standard mode for project: {}", projectId);
            processedCount = processStandardMode(projectId);
        }
        
        logger.info("Material code suggestion completed for project: {}. Processed {} records.", 
                projectId, processedCount);
    }
    
    private int processStandardMode(String projectId) {
        List<InspireLockedCodesReplacedBy> records = 
                inspireLockedCodesReplacedByRepository.findByProjectIdAndReplacementCodeIsNull(projectId);
        
        int updatedCount = 0;
        for (InspireLockedCodesReplacedBy record : records) {
            String notes = record.getSupersededNotes();
            if (notes != null && !notes.isEmpty()) {
                String normalizedText = normalizeText(notes);
                String potentialCode = extractPotentialCode(normalizedText);
                
                if (potentialCode != null) {
                    record.setReplacementCode(CODE_PREFIX + potentialCode);
                    inspireLockedCodesReplacedByRepository.save(record);
                    updatedCount++;
                }
            }
        }
        
        return updatedCount;
    }
    
    private int processEamFeedingMode(String projectId) {
        List<NpeamLockedCompBase> records = 
                npeamLockedCompBaseRepository.findByProjectIdAndReplacementCodeIsNull(projectId);
        
        int updatedCount = 0;
        for (NpeamLockedCompBase record : records) {
            String notes = record.getSupersededNotes();
            if (notes != null && !notes.isEmpty()) {
                String normalizedText = normalizeText(notes);
                String potentialCode = extractPotentialCode(normalizedText);
                
                if (potentialCode != null) {
                    record.setReplacementCode(CODE_PREFIX + potentialCode);
                    npeamLockedCompBaseRepository.save(record);
                    updatedCount++;
                }
            }
        }
        
        return updatedCount;
    }
    
    private String normalizeText(String text) {
        if (text == null) {
            return "";
        }
        
        // Replace specific Italian terms
        String normalized = text.toLowerCase()
                .replace("codice", "code")
                .replace("materiale", "material")
                .replace("ricambio", "spare")
                .replace("sostituito", "replaced")
                .replace("sostituire", "replace")
                .replace("con", "with");
        
        // Replace special characters with spaces
        normalized = normalized.replaceAll("[^a-zA-Z0-9]", " ");
        
        // Normalize multiple spaces to single spaces
        normalized = normalized.replaceAll("\\s+", " ");
        
        // Trim leading and trailing spaces
        return normalized.trim();
    }
    
    private String extractPotentialCode(String normalizedText) {
        if (normalizedText == null || normalizedText.isEmpty()) {
            return null;
        }
        
        // Pattern for letter-number or number-letter combinations
        Pattern pattern = Pattern.compile("\\b[a-zA-Z]+\\d+\\w*\\b|\\b\\d+[a-zA-Z]+\\w*\\b");
        Matcher matcher = pattern.matcher(normalizedText);
        
        if (matcher.find()) {
            String potentialCode = matcher.group();
            
            // Limit to maximum length
            if (potentialCode.length() > MAX_CODE_LENGTH) {
                potentialCode = potentialCode.substring(0, MAX_CODE_LENGTH);
            }
            
            return potentialCode;
        }
        
        return null;
    }
}