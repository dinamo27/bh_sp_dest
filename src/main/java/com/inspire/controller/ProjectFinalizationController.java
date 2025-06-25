package com.inspire.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
@Api(value = "Project Finalization Controller", description = "Operations for finalizing SPIR projects")
public class ProjectFinalizationController {

    private final ProjectFinalizationService projectFinalizationService;

    public ProjectFinalizationController(ProjectFinalizationService projectFinalizationService) {
        this.projectFinalizationService = projectFinalizationService;
    }

    @PostMapping("/{projectId}/finalize")
    @ApiOperation(value = "Reset and finalize a SPIR project", response = Map.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Project successfully finalized"),
            @ApiResponse(code = 500, message = "Internal server error occurred during finalization")
    })
    public ResponseEntity<Map<String, Object>> finalizeProject(@PathVariable Long projectId) {
        try {
            int result = projectFinalizationService.resetAndFinalizeSPIRProject(projectId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", result == 0);
            response.put("message", result == 0 ? "Project successfully finalized" : "Failed to finalize project");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to finalize project: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}