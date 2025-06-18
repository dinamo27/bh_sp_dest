package com.example.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.exception.ProjectNotFoundException;
import com.example.service.ProjectResetService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/api/projects")
@Api(value = "Project Reset Controller", description = "Operations for technical reset of projects")
public class ProjectResetController {

    private final ProjectResetService projectResetService;

    public ProjectResetController(ProjectResetService projectResetService) {
        this.projectResetService = projectResetService;
    }

    @PostMapping("/{projectId}/reset")
    @ApiOperation(value = "Reset a project to a clean state", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Project successfully reset"),
            @ApiResponse(code = 404, message = "Project not found"),
            @ApiResponse(code = 500, message = "Internal server error during reset operation")
    })
    public ResponseEntity<String> resetProject(@PathVariable Integer projectId) {
        try {
            projectResetService.technicalResetProject(projectId);
            return ResponseEntity.ok("Project successfully reset");
        } catch (ProjectNotFoundException e) {
            throw e;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to reset project: " + e.getMessage());
        }
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<String> handleProjectNotFoundException(ProjectNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}