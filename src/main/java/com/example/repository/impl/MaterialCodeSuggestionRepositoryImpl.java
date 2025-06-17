package com.example.repository.impl;

import com.example.repository.MaterialCodeSuggestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
    
    @Override
    public boolean isProjectInInspire(String projectId) {
        try {
            Number count = (Number) entityManager.createNativeQuery(
                "SELECT COUNT(1) FROM inspire_project WHERE project_id = :projectId")
                .setParameter("projectId", projectId)
                .getSingleResult();
            return count.intValue() > 0;
        } catch (Exception e) {
            logger.error("Error checking if project is in inspire: {}", projectId, e);
            return false;
        }
    }
    
    @Override
    @Transactional
    public int resetMarkFieldInInspireParts(String projectId) {
        try {
            int updatedRows = entityManager.createNativeQuery(
                "UPDATE inspire_parts SET mark = NULL WHERE project_id = :projectId")
                .setParameter("projectId", projectId)
                .executeUpdate();
            logger.debug("Reset mark field for {} rows in inspire_parts for project: {}", updatedRows, projectId);
            return updatedRows;
        } catch (Exception e) {
            logger.error("Error resetting mark field in inspire_parts for project: {}", projectId, e);
            throw e;
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getInspireLockedCodesForProcessing(String projectId) {
        try {
            List<Map<String, Object>> results = entityManager.createNativeQuery(
                "SELECT id, superseded_notes FROM inspire_locked_codes_replaced_by " +
                "WHERE project_id = :projectId AND replaced_by IS NULL AND superseded_notes IS NOT NULL")
                .setParameter("projectId", projectId)
                .getResultList();
            logger.debug("Found {} inspire locked codes for processing in project: {}", results.size(), projectId);
            return results;
        } catch (Exception e) {
            logger.error("Error getting inspire locked codes for project: {}", projectId, e);
            throw e;
        }
    }
    
    @Override
    @Transactional
    public int updateInspireLockedCodesReplacedBy(Long id, String replacedBy) {
        try {
            int updatedRows = entityManager.createNativeQuery(
                "UPDATE inspire_locked_codes_replaced_by SET replaced_by = :replacedBy, to_be_verified = 1 " +
                "WHERE id = :id")
                .setParameter("id", id)
                .setParameter("replacedBy", replacedBy)
                .executeUpdate();
            logger.debug("Updated inspire locked code with id: {}, replaced by: {}", id, replacedBy);
            return updatedRows;
        } catch (Exception e) {
            logger.error("Error updating inspire locked code with id: {}", id, e);
            throw e;
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getNpeamLockedCompBaseForProcessing(String projectId) {
        try {
            List<Map<String, Object>> results = entityManager.createNativeQuery(
                "SELECT id, superseded_notes FROM npeam_locked_comp_base " +
                "WHERE project_id = :projectId AND suggested_material_code IS NULL AND superseded_notes IS NOT NULL")
                .setParameter("projectId", projectId)
                .getResultList();
            logger.debug("Found {} npeam locked components for processing in project: {}", results.size(), projectId);
            return results;
        } catch (Exception e) {
            logger.error("Error getting npeam locked components for project: {}", projectId, e);
            throw e;
        }
    }
    
    @Override
    @Transactional
    public int updateNpeamLockedCompBaseSuggestedMaterialCode(Long id, String suggestedMaterialCode) {
        try {
            int updatedRows = entityManager.createNativeQuery(
                "UPDATE npeam_locked_comp_base SET suggested_material_code = :suggestedMaterialCode, to_be_verified = 1 " +
                "WHERE id = :id")
                .setParameter("id", id)
                .setParameter("suggestedMaterialCode", suggestedMaterialCode)
                .executeUpdate();
            logger.debug("Updated npeam locked component with id: {}, suggested material code: {}", id, suggestedMaterialCode);
            return updatedRows;
        } catch (Exception e) {
            logger.error("Error updating npeam locked component with id: {}", id, e);
            throw e;
        }
    }
    
    public String normalizeText(String text) {
        if (text == null) {
            return null;
        }
        
        String normalized = text.replace("VALVOLE", "VAL VOLE")
                               .replace("DISTINTA", "DIS TINTA");
        
        normalized = normalized.replaceAll("[,.:;/\\\\(){}\[\]<>!?+*=&%$#@^_|~`\"'-]", " ");
        
        normalized = normalized.replaceAll("\\s+", " ").trim();
        
        return normalized;
    }
    
    public String extractMaterialCode(String normalizedText) {
        if (normalizedText == null || normalizedText.isEmpty()) {
            return null;
        }
        
        Matcher matcher = MATERIAL_CODE_PATTERN.matcher(normalizedText);
        if (matcher.find()) {
            String potentialCode = matcher.group();
            
            if (potentialCode.length() > MAX_CODE_LENGTH) {
                potentialCode = potentialCode.substring(0, MAX_CODE_LENGTH);
            }
            
            return CODE_PREFIX + potentialCode;
        }
        
        return null;
    }
    
    public String processMaterialCodeFromText(String text) {
        try {
            String normalizedText = normalizeText(text);
            String materialCode = extractMaterialCode(normalizedText);
            logger.debug("Processed text for material code. Original: '{}', Normalized: '{}', Extracted: '{}'", 
                    text, normalizedText, materialCode);
            return materialCode;
        } catch (Exception e) {
            logger.error("Error processing material code from text: '{}'", text, e);
            return null;
        }
    }
}