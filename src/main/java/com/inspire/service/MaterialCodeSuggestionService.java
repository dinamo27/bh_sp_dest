package com.inspire.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inspire.repository.InspireProjectRepository;
import com.inspire.repository.InspireLockedCodesRepository;
import com.inspire.repository.NpeamLockedCompBaseRepository;
import com.inspire.repository.InspirePartsRepository;
import com.inspire.model.ProcessingResult;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface MaterialCodeSuggestionService {
    /**
     * Process material code suggestions for the specified project
     * 
     * @param projectId The ID of the project to process
     * @return ProcessingResult containing operation details
     */
    ProcessingResult suggestReplacementMaterialCodes(String projectId);
}

@Service
class MaterialCodeSuggestionServiceImpl implements MaterialCodeSuggestionService {
    private static final Logger logger = LoggerFactory.getLogger(MaterialCodeSuggestionServiceImpl.class);
    private static final Pattern MATERIAL_CODE_PATTERN = Pattern.compile("([A-Z0-9]{3,10})");
    
    @Autowired
    private InspireProjectRepository inspireProjectRepository;
    
    @Autowired
    private InspireLockedCodesRepository inspireLockedCodesRepository;
    
    @Autowired
    private NpeamLockedCompBaseRepository npeamLockedCompBaseRepository;
    
    @Autowired
    private InspirePartsRepository inspirePartsRepository;
    
    @Override
    @Transactional
    public ProcessingResult suggestReplacementMaterialCodes(String projectId) {
        logger.info("Starting material code suggestion process for project: {}", projectId);
        ProcessingResult result = new ProcessingResult();
        
        try {
            boolean projectExists = inspireProjectRepository.existsById(projectId);
            int updatedRows = 0;
            
            if (projectExists) {
                logger.info("Processing in INSPIRE mode for project: {}", projectId);
                updatedRows = processInspireMode(projectId);
            } else {
                logger.info("Processing in NPEAM mode for project: {}", projectId);
                updatedRows = processNpeamMode(projectId);
            }
            
            // Reset mark field in inspire_parts table
            inspirePartsRepository.resetMarkField(projectId);
            
            result.setSuccess(true);
            result.setAffectedRows(updatedRows);
            result.setMessage("Successfully processed material code suggestions for project: " + projectId);
            logger.info("Material code suggestion process completed successfully for project: {}. Updated {} rows.", 
                    projectId, updatedRows);
            
        } catch (Exception e) {
            logger.error("Error processing material code suggestions for project: {}", projectId, e);
            result.setSuccess(false);
            result.setMessage("Error processing material code suggestions: " + e.getMessage());
            result.setException(e);
        }
        
        return result;
    }
    
    private int processInspireMode(String projectId) {
        List<Map<String, Object>> records = inspireLockedCodesRepository.findByProjectId(projectId);
        int updatedRows = 0;
        
        for (Map<String, Object> record : records) {
            String descriptiveNotes = (String) record.get("descriptive_notes");
            String normalizedNotes = normalizeText(descriptiveNotes);
            String extractedCode = extractMaterialCode(normalizedNotes);
            
            if (extractedCode != null) {
                String partId = (String) record.get("part_id");
                inspireLockedCodesRepository.updateReplacementCode(partId, projectId, extractedCode);
                updatedRows++;
            }
        }
        
        return updatedRows;
    }
    
    private int processNpeamMode(String projectId) {
        List<Map<String, Object>> records = npeamLockedCompBaseRepository.findByProjectId(projectId);
        int updatedRows = 0;
        
        for (Map<String, Object> record : records) {
            String descriptiveNotes = (String) record.get("descriptive_notes");
            String normalizedNotes = normalizeText(descriptiveNotes);
            String extractedCode = extractMaterialCode(normalizedNotes);
            
            if (extractedCode != null) {
                String partId = (String) record.get("part_id");
                npeamLockedCompBaseRepository.updateReplacementCode(partId, projectId, extractedCode);
                updatedRows++;
            }
        }
        
        return updatedRows;
    }
    
    private String normalizeText(String text) {
        if (text == null) {
            return "";
        }
        
        // Convert to uppercase for consistent pattern matching
        String normalized = text.toUpperCase();
        
        // Remove special characters and extra spaces
        normalized = normalized.replaceAll("[^A-Z0-9\\s]", " ");
        normalized = normalized.replaceAll("\\s+", " ").trim();
        
        return normalized;
    }
    
    private String extractMaterialCode(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        
        Matcher matcher = MATERIAL_CODE_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        return null;
    }
}