package com.inspire.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inspire.service.MaterialCodeSuggestionService;

@RestController
@RequestMapping("/api/material-codes")
public class MaterialCodeSuggestionController {

    private final MaterialCodeSuggestionService materialCodeSuggestionService;
    
    public MaterialCodeSuggestionController(MaterialCodeSuggestionService materialCodeSuggestionService) {
        this.materialCodeSuggestionService = materialCodeSuggestionService;
    }
    
    @PostMapping("/suggest")
    public ResponseEntity<?> suggestMaterialCodes(@RequestParam String projectId) {
        Object result = materialCodeSuggestionService.suggestReplacementMaterialCodes(projectId);
        return ResponseEntity.ok(result);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleExceptions(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error processing material code suggestions: " + ex.getMessage());
    }
}