package com.example.project.controller;

import com.example.project.dto.InspireOmProjectUpdateRequest;
import com.example.project.dto.InspireOmProjectUpdateResponse;
import com.example.project.exception.BusinessException;
import com.example.project.service.InspireOmProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Objects;

@RestController
public class InspireOmProjectUpdateController {

    private final InspireOmProjectService inspireOmProjectService;

    public InspireOmProjectUpdateController(InspireOmProjectService inspireOmProjectService) {
        this.inspireOmProjectService = inspireOmProjectService;
    }

    @PostMapping("/api/inspire-om-project/update/{spirProjectId}")
    public ResponseEntity<InspireOmProjectUpdateResponse> updateInspireOmProject(
            @PathVariable Long spirProjectId,
            @Valid @RequestBody InspireOmProjectUpdateRequest request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(getErrorResponse(bindingResult));
        }

        if (!isValidspirProjectId(spirProjectId)) {
            return ResponseEntity.badRequest().body(getErrorResponse("Invalid spirProjectId"));
        }

        if (!isValidLogId(request.getLogId())) {
            return ResponseEntity.badRequest().body(getErrorResponse("Invalid logId"));
        }

        if (!isValidTempFields(request.getTemp1()) || !isValidTemp2(request.getTemp2()) || !isValidTemp3(request.getTemp3()) || !isValidTemp4(request.getTemp4())) {
            return ResponseEntity.badRequest().body(getErrorResponse("Invalid temp fields"));
        }

        try {
            InspireOmProjectUpdateResponse response = inspireOmProjectService.updateInspireOmProject(
                    spirProjectId,
                    request.getLogId(),
                    request.getTemp1(),
                    request.getTemp2(),
                    request.getTemp3(),
                    request.getTemp4());

            return ResponseEntity.ok(response);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getErrorResponse(e.getMessage()));
        }
    }

    private boolean isValidspirProjectId(Long spirProjectId) {
        return Objects.nonNull(spirProjectId) && spirProjectId > 0;
    }

    private boolean isValidLogId(Long logId) {
        return Objects.nonNull(logId) && logId > 0;
    }

    private boolean isValidTemp1(String temp1) {
        return Objects.nonNull(temp1) && !temp1.isEmpty();
    }

    private boolean isValidTemp2(String temp2) {
        return Objects.nonNull(temp2) && !temp2.isEmpty();
    }

    private boolean isValidTemp3(String temp3) {
        return Objects.nonNull(temp3) && !temp3.isEmpty();
    }

    private boolean isValidTemp4(String temp4) {
        return Objects.nonNull(temp4) && !temp4.isEmpty();
    }

    private InspireOmProjectUpdateResponse getErrorResponse(String message) {
        InspireOmProjectUpdateResponse response = new InspireOmProjectUpdateResponse();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }

    private InspireOmProjectUpdateResponse getErrorResponse(BindingResult bindingResult) {
        InspireOmProjectUpdateResponse response = new InspireOmProjectUpdateResponse();
        response.setSuccess(false);
        response.setMessage(bindingResult.getAllErrors().get(0).getDefaultMessage());
        return response;
    }
}