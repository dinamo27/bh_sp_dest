package com.inspire.repository.impl;

import com.inspire.repository.MaterialCodeSuggestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository
public class MaterialCodeSuggestionRepositoryImpl implements MaterialCodeSuggestionRepository {
    private static final Logger logger = LoggerFactory.getLogger(MaterialCodeSuggestionRepositoryImpl.class);
    private static final String CODE_PREFIX = "inSPIRe suggested from superseded notes: ";
    private static final int MAX_CODE_LENGTH = 40;
    private static final Pattern MATERIAL_CODE_PATTERN = Pattern.compile("[A-Za-z][0-9]|[0-9][A-Za-z]");
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Override
    public boolean isProjectInInspire(String projectId) {
        String sql = "SELECT COUNT(1) FROM inspire_project WHERE project_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, projectId);
        return count != null && count > 0;
    }
    
    @Override
    @Transactional
    public int resetMarkFieldInInspireParts(String projectId) {
        String sql = "UPDATE inspire_parts SET mark = NULL WHERE project_id = ?";
        return jdbcTemplate.update(sql, projectId);
    }
    
    @Override
    public List<Map<String, Object>> getInspireLockedCodesReplacedByForProcessing(String projectId) {
        String sql = "SELECT id, superseded_notes FROM inspire_locked_codes_replaced_by " +
                     "WHERE project_id = ? AND replaced_by IS NULL AND superseded_notes IS NOT NULL";
        return jdbcTemplate.queryForList(sql, projectId);
    }
    
    @Override
    public List<Map<String, Object>> getNpeamLockedCompBaseForProcessing(String projectId) {
        String sql = "SELECT id, superseded_notes FROM npeam_locked_comp_base " +
                     "WHERE project_id = ? AND suggested_material_code IS NULL AND superseded_notes IS NOT NULL";
        return jdbcTemplate.queryForList(sql, projectId);
    }
    
    @Override
    @Transactional
    public int updateInspireLockedCodesReplacedBy(Long id, String replacedBy) {
        String sql = "UPDATE inspire_locked_codes_replaced_by SET replaced_by = ?, to_be_verified = 1 WHERE id = ?";
        return jdbcTemplate.update(sql, replacedBy, id);
    }
    
    @Override
    @Transactional
    public int updateNpeamLockedCompBase(Long id, String suggestedMaterialCode) {
        String sql = "UPDATE npeam_locked_comp_base SET suggested_material_code = ?, to_be_verified = 1 WHERE id = ?";
        return jdbcTemplate.update(sql, suggestedMaterialCode, id);
    }
    
    public String normalizeText(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        
        // Replace specific Italian terms
        String normalized = text.replace("VALVOLE", "VAL VOLE")
                               .replace("DISTINTA", "DIS TINTA");
        
        // Replace special characters with spaces
        normalized = normalized.replaceAll("[,.:;/\\\\(){}\[\]<>!?+*=&%$#@^_|~`\"'-]", " ");
        
        // Normalize spaces
        normalized = normalized.replaceAll("\\s+", " ").trim();
        
        return normalized;
    }
    
    public String extractMaterialCode(String normalizedText) {
        if (normalizedText == null || normalizedText.isEmpty()) {
            return null;
        }
        
        Matcher matcher = MATERIAL_CODE_PATTERN.matcher(normalizedText);
        
        if (matcher.find()) {
            // Extract more characters around the match to get a meaningful code
            int startPos = Math.max(0, matcher.start() - 5);
            int endPos = Math.min(normalizedText.length(), matcher.end() + 35);
            String potentialCode = normalizedText.substring(startPos, endPos).trim();
            
            // Limit to MAX_CODE_LENGTH characters
            if (potentialCode.length() > MAX_CODE_LENGTH) {
                potentialCode = potentialCode.substring(0, MAX_CODE_LENGTH);
            }
            
            return potentialCode;
        }
        
        return null;
    }
    
    public String formatExtractedCode(String extractedCode) {
        if (extractedCode == null || extractedCode.isEmpty()) {
            return null;
        }
        
        return CODE_PREFIX + extractedCode;
    }
    
    public String processTextForMaterialCode(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        
        String normalizedText = normalizeText(text);
        String extractedCode = extractMaterialCode(normalizedText);
        
        if (extractedCode != null) {
            return formatExtractedCode(extractedCode);
        }
        
        return null;
    }
}