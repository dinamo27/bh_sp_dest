package com.inspire.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inspire.service.MaterialCodeSuggestionService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/material-codes")
public class MaterialCodeSuggestionController {
    
    private static final Logger logger = LoggerFactory.getLogger(MaterialCodeSuggestionController.class);
    private final MaterialCodeSuggestionService materialCodeSuggestionService;
    
    public MaterialCodeSuggestionController(MaterialCodeSuggestionService materialCodeSuggestionService) {
        this.materialCodeSuggestionService = materialCodeSuggestionService;
    }
    
    @PostMapping("/suggest/{projectId}")
    public ResponseEntity<Map<String, String>> suggestReplacementMaterialCodes(@PathVariable String projectId) {
        logger.info("Received request to suggest replacement material codes for project: {}", projectId);
        
        try {
            materialCodeSuggestionService.suggestReplacementMaterialCodes(projectId);
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Material code suggestion process initiated for project: " + projectId);
            
            logger.info("Material code suggestion process initiated successfully for project: {}", projectId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid project ID: {}", projectId, e);
            return ResponseEntity.badRequest().body(createErrorResponse("Invalid project ID: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Error suggesting replacement material codes for project: {}", projectId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error processing material code suggestion: " + e.getMessage()));
        }
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        logger.error("Unhandled exception in MaterialCodeSuggestionController", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Internal server error: " + e.getMessage()));
    }
    
    private Map<String, String> createErrorResponse(String errorMessage) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", errorMessage);
        return response;
    }
}