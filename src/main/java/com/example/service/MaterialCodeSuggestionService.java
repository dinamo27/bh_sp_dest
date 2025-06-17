package com.example.service;

/**
 * Service interface for identifying and suggesting replacement material codes
 */
public interface MaterialCodeSuggestionService {
    /**
     * Identifies and suggests replacement material codes for a specific project
     * @param projectId the project ID to process
     * @return the number of records updated with suggested material codes
     * @throws Exception if an error occurs during processing
     */
    int identifyAndSuggestReplacementMaterialCodes(String projectId) throws Exception;
}